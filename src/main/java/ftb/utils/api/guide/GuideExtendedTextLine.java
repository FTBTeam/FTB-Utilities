package ftb.utils.api.guide;

import com.google.gson.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.notification.*;
import latmod.lib.LMUtils;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

/**
 * Created by LatvianModder on 20.03.2016.
 */
public class GuideExtendedTextLine extends GuideTextLine
{
	private IChatComponent text;
	private ClickAction clickAction;
	private List<IChatComponent> hover;
	private String imageURL;
	private GuideImage image;
	
	public GuideExtendedTextLine(GuidePage c, IChatComponent cc)
	{
		super(c, null);
		text = cc;
	}
	
	public IChatComponent getText()
	{ return text; }
	
	public ClickAction getClickAction()
	{ return clickAction; }
	
	public List<IChatComponent> getHover()
	{ return hover; }
	
	@SideOnly(Side.CLIENT)
	public GuideImage getImage()
	{
		if(imageURL == null) return null;
		
		if(image != null && image.width > 0) return image;
		else if(image == null) image = new GuideImage();
		
		try
		{
			File file = new File(FTBLib.folderModpack, "images/" + imageURL);
			if(FTBLib.DEV_ENV) FTBLib.dev_logger.info("Loading Guide image: " + file.getAbsolutePath());
			BufferedImage img = ImageIO.read(file);
			image.texture = FTBLibClient.mc.getTextureManager().getDynamicTextureLocation("ftbu_guide/" + imageURL, new DynamicTexture(img));
			image.width = img.getWidth();
			image.height = img.getHeight();
		}
		catch(Exception e)
		{
			image.texture = null;
			image.width = 1;
			image.height = 1;
			e.printStackTrace();
		}
		
		return image;
	}
	
	public void setJson(JsonElement e)
	{
		JsonObject o = e.getAsJsonObject();
		
		text = o.has("text") ? JsonHelper.deserializeICC(o.get("text")) : null;
		
		if(o.has("click"))
		{
			clickAction = new ClickAction();
			clickAction.setJson(o.get("click"));
		}
		else clickAction = null;
		
		if(o.has("hover"))
		{
			hover = new ArrayList<>();
			
			JsonElement e1 = o.get("hover");
			
			if(e1.isJsonPrimitive()) hover.add(JsonHelper.deserializeICC(e1));
			else
			{
				for(JsonElement e2 : o.get("hover").getAsJsonArray())
				{
					hover.add(JsonHelper.deserializeICC(e2));
				}
			}
			
			if(hover.isEmpty()) hover = null;
		}
		else hover = null;
		
		setImage(o.has("image") ? o.get("image").getAsString() : null);
		
		if(imageURL != null)
		{
			if(o.has("scale"))
			{
				if(image == null) image = new GuideImage();
				image.displayScale = o.get("scale").getAsDouble();
			}
			else
			{
				if(o.has("width"))
				{
					if(image == null) image = new GuideImage();
					image.displayWidth = o.get("width").getAsDouble();
				}
				
				if(o.has("height"))
				{
					if(image == null) image = new GuideImage();
					image.displayHeight = o.get("height").getAsDouble();
				}
			}
		}
	}
	
	public JsonElement getJson()
	{
		JsonObject o = new JsonObject();
		if(text != null) o.add("text", JsonHelper.serializeICC(text));
		
		if(clickAction != null)
		{
			o.add("click", clickAction.getJson());
		}
		
		if(hover != null && !hover.isEmpty())
		{
			if(hover.size() == 1)
			{
				o.add("hover", JsonHelper.serializeICC(hover.get(0)));
			}
			else
			{
				JsonArray a = new JsonArray();
				for(IChatComponent c : hover)
				{
					a.add(JsonHelper.serializeICC(c));
				}
				
				o.add("hover", a);
			}
		}
		
		if(imageURL != null && !imageURL.isEmpty()) o.add("image", new JsonPrimitive(imageURL));
		return o;
	}
	
	public void setClickAction(ClickAction a)
	{
		clickAction = a;
		if(hover == null)
		{
			if(a.type == ClickActionType.FILE || a.type == ClickActionType.URL)
			{
				hover = Collections.singletonList((IChatComponent) new ChatComponentText(a.data.getAsString()));
			}
			else if(a.type == ClickActionType.CMD)
			{
				hover = Collections.singletonList((IChatComponent) new ChatComponentText("/" + a.data.getAsString()));
			}
		}
	}
	
	public void setHover(List<IChatComponent> h)
	{ hover = h; }
	
	public void setImage(String img)
	{
		String imageURL0 = imageURL == null ? null : (imageURL + "");
		imageURL = img;
		if(!LMUtils.areObjectsEqual(imageURL0, imageURL, true)) image = null;
		if(imageURL != null) text = null;
	}
}