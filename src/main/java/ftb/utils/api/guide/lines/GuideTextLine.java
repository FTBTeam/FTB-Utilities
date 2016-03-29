package ftb.utils.api.guide.lines;

import com.google.gson.*;
import cpw.mods.fml.relauncher.*;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.mod.client.gui.guide.*;
import net.minecraft.util.*;

/**
 * Created by LatvianModder on 20.03.2016.
 */
public class GuideTextLine implements IJsonSerializable
{
	public static GuideTextLine get(GuidePage c, JsonElement e)
	{
		if(e == null || e.isJsonNull()) return null;
		else if(e.isJsonPrimitive())
		{
			String s = e.getAsString();
			return s.trim().isEmpty() ? null : new GuideTextLine(c, s);
		}
		else
		{
			JsonObject o = e.getAsJsonObject();
			
			GuideExtendedTextLine l;
			
			if(o.has("image"))
			{
				l = new GuideImageLine(c);
			}
			else
			{
				l = new GuideExtendedTextLine(c, null);
			}
			
			l.func_152753_a(o);
			return l;
		}
	}
	
	public final GuidePage page;
	private String text;
	
	public GuideTextLine(GuidePage c, String s)
	{
		page = c;
		text = s;
	}
	
	public IChatComponent getText()
	{ return new ChatComponentText(text); }
	
	@SideOnly(Side.CLIENT)
	public ButtonGuideTextLine createWidget(GuiGuide gui)
	{ return new ButtonGuideTextLine(gui, this); }
	
	public void func_152753_a(JsonElement e)
	{ text = e.getAsString(); }
	
	public JsonElement getSerializableElement()
	{ return new JsonPrimitive(text); }
}