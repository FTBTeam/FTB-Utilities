package ftb.utils.api.guide;

import com.google.gson.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.item.ItemStackSerializer;
import ftb.lib.mod.client.gui.GuiViewImage;
import ftb.utils.mod.client.gui.guide.GuiGuide;
import latmod.lib.LMUtils;
import latmod.lib.json.IJsonObject;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.net.*;
import java.util.List;

public abstract class GuideLink implements IJsonObject
{
	public enum Type
	{
		URL,
		IMAGE,
		RECIPE;
		
		public boolean isText()
		{ return this == URL; }
		
		public boolean isImage()
		{ return this == IMAGE; }
	}
	
	public final Type type;
	public String link;
	protected IChatComponent title;
	public IChatComponent hover[];
	
	private GuideLink(Type t)
	{
		type = t;
	}
	
	public final JsonElement getJson()
	{
		if(isSimple()) return (link == null) ? JsonNull.INSTANCE : new JsonPrimitive(link);
		
		JsonObject o = new JsonObject();
		if(title != null) o.add("title", JsonHelper.serializeICC(title));
		if(hover != null && hover.length > 0)
		{
			if(hover.length == 1) o.add("hover", JsonHelper.serializeICC(hover[0]));
			else
			{
				JsonArray a = new JsonArray();
				for(int i = 0; i < hover.length; i++)
					a.add(JsonHelper.serializeICC(hover[i]));
				o.add("hover", a);
			}
		}
		getJsonObj(o);
		return o;
	}
	
	public final void setJson(JsonElement e)
	{
		if(e == null) return;
		else if(e.isJsonNull())
		{
			link = null;
			return;
		}
		else if(e.isJsonPrimitive())
		{
			link = e.getAsString();
			return;
		}
		else if(!e.isJsonObject()) return;
		
		JsonObject o = e.getAsJsonObject();
		link = o.get("link").getAsString();
		title = JsonHelper.deserializeICC(o.get("title"));
		
		if(o.has("hover"))
		{
			JsonElement a = o.get("hover");
			
			if(a.isJsonArray())
			{
				JsonArray a1 = a.getAsJsonArray();
				hover = new IChatComponent[a1.size()];
				for(int i = 0; i < hover.length; i++)
					hover[i] = JsonHelper.deserializeICC(a1.get(i));
			}
			else hover = new IChatComponent[] {JsonHelper.deserializeICC(a)};
		}
		else hover = null;
		
		setJsonObj(o);
	}
	
	public boolean isSimple()
	{ return title == null && (hover == null || hover.length == 0); }
	
	public IChatComponent getTitle()
	{
		if(title != null) return title;
		return new ChatComponentText(link);
	}
	
	public void addHoverText(List<String> l)
	{
		if(hover != null)
		{
			for(IChatComponent c : hover)
			{
				if(c == null) l.add("");
				else l.add(c.getFormattedText());
			}
		}
	}
	
	public abstract void getJsonObj(JsonObject o);
	public abstract void setJsonObj(JsonObject o);
	public abstract void onClicked(GuiGuide gui);
	
	public static GuideLink newInstance(Type t)
	{
		if(t == Type.URL) return new GuideURL();
		else if(t == Type.IMAGE) return new GuideImage();
		else if(t == Type.RECIPE) return new GuideRecipe();
		return null;
	}
	
	public static class GuideURL extends GuideLink
	{
		public GuideURL()
		{
			super(Type.URL);
		}
		
		public void getJsonObj(JsonObject o)
		{
		}
		
		public void setJsonObj(JsonObject o)
		{
		}
		
		@SideOnly(Side.CLIENT)
		public void onClicked(GuiGuide gui)
		{
			try { LMUtils.openURI(new URI(link)); }
			catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	public static class GuideImage extends GuideLink
	{
		public boolean isURL = false;
		private TextureCoords texture = null;
		
		public GuideImage()
		{
			super(Type.IMAGE);
		}
		
		@SideOnly(Side.CLIENT)
		public TextureCoords getTexture()
		{
			if(texture != null) return texture;
			
			try
			{
				BufferedImage img;
				if(isURL) img = ImageIO.read(new URL(link));
				else img = ImageIO.read(new File(FTBLib.folderModpack, link));
				
				ResourceLocation location = FTBLibClient.mc.getTextureManager().getDynamicTextureLocation("ftbu/" + link, new DynamicTexture(img));
				texture = new TextureCoords(location, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
			}
			catch(Exception e)
			{
				texture = TextureCoords.nullTexture;
			}
			
			return texture;
		}
		
		public void getJsonObj(JsonObject o)
		{
			if(o.has("isURL")) isURL = o.get("isURL").getAsBoolean();
			else isURL = false;
		}
		
		public void setJsonObj(JsonObject o)
		{
			if(isURL) o.add("isURL", new JsonPrimitive(true));
		}
		
		@SideOnly(Side.CLIENT)
		public void onClicked(GuiGuide gui)
		{
			TextureCoords tc = getTexture();
			if(tc != null && tc.isValid()) FTBLibClient.openGui(new GuiViewImage(gui, tc));
		}
		
		public boolean isSimple()
		{ return !isURL && super.isSimple(); }
	}
	
	public static class GuideRecipe extends GuideLink
	{
		private static Boolean hasNEI = null;
		private static Method method = null;
		
		public ItemStack itemStack = null;
		
		public GuideRecipe()
		{
			super(Type.RECIPE);
		}
		
		public void getJsonObj(JsonObject o)
		{
		}
		
		public void setJsonObj(JsonObject o)
		{
			itemStack = ItemStackSerializer.parseItem(link);
			
			if(itemStack != null && !o.has("title"))
			{
				title = new ChatComponentText(itemStack.getDisplayName());
			}
		}
		
		public void onClicked(GuiGuide gui)
		{
			if(itemStack != null)
			{
				if(hasNEI == null)
				{
					hasNEI = Boolean.FALSE;
					
					try
					{
						Class<?> c = Class.forName("codechicken.nei.recipe.GuiCraftingRecipe");
						method = c.getMethod("openRecipeGui", String.class, Object[].class);
						if(method != null) hasNEI = Boolean.TRUE;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if(hasNEI.booleanValue())
				{
					try { method.invoke(null, "item", new Object[] {itemStack}); }
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}