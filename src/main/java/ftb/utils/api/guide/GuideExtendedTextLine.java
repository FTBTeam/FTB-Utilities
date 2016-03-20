package ftb.utils.api.guide;

import com.google.gson.*;
import ftb.lib.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.notification.ClickAction;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.*;

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
	private TextureCoords texture;
	
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
	public TextureCoords getTexture(File imageFolder)
	{
		if(imageURL == null || texture != null || imageFolder == null) return texture;
		
		try
		{
			BufferedImage img = ImageIO.read(new File(imageFolder, imageURL));
			ResourceLocation location = FTBLibClient.mc.getTextureManager().getDynamicTextureLocation("ftbu_guide/" + imageURL, new DynamicTexture(img));
			texture = new TextureCoords(location, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
		}
		catch(Exception e)
		{
			texture = TextureCoords.nullTexture;
		}
		
		return texture;
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
			List<IChatComponent> l = new ArrayList<>();
			
			JsonElement e1 = o.get("hover");
			
			if(e1.isJsonPrimitive()) l.add(JsonHelper.deserializeICC(e1));
			else
			{
				for(JsonElement e2 : o.get("hover").getAsJsonArray())
				{
					l.add(JsonHelper.deserializeICC(e2));
				}
			}
			
			if(!l.isEmpty()) hover = Collections.unmodifiableList(l);
		}
		else hover = null;
		
		imageURL = o.has("image") ? o.get("image").getAsString() : null;
		if(imageURL != null) text = null;
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
}