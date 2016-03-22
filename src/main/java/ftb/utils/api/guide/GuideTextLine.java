package ftb.utils.api.guide;

import com.google.gson.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.notification.ClickAction;
import latmod.lib.json.IJsonObject;
import net.minecraft.util.*;

import java.util.List;

/**
 * Created by LatvianModder on 20.03.2016.
 */
public class GuideTextLine implements IJsonObject
{
	public static GuideTextLine get(GuidePage c, JsonElement e)
	{
		if(e == null || e.isJsonNull()) return null;
		else if(e.isJsonPrimitive()) return new GuideTextLine(c, e.getAsString());
		else
		{
			GuideExtendedTextLine l = new GuideExtendedTextLine(c, null);
			l.setJson(e);
			return l;
		}
	}
	
	public final GuidePage page;
	private String text;
	
	public GuideTextLine(GuidePage c, String cc)
	{
		page = c;
		text = cc;
	}
	
	public IChatComponent getText()
	{ return new ChatComponentText(text); }
	
	public ClickAction getClickAction()
	{ return null; }
	
	public List<IChatComponent> getHover()
	{ return null; }
	
	@SideOnly(Side.CLIENT)
	public GuideImage getImage()
	{ return null; }
	
	public void setJson(JsonElement e)
	{ text = e.getAsString(); }
	
	public JsonElement getJson()
	{ return new JsonPrimitive(text); }
}