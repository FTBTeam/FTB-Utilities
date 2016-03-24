package ftb.utils.api.guide.lines;

import com.google.gson.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.JsonHelper;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.notification.ClickAction;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.mod.client.gui.guide.*;
import net.minecraft.util.IChatComponent;

import java.util.*;

/**
 * Created by LatvianModder on 20.03.2016.
 */
public class GuideExtendedTextLine extends GuideTextLine
{
	protected IChatComponent text;
	private ClickAction clickAction;
	private List<IChatComponent> hover;
	
	public GuideExtendedTextLine(GuidePage c, IChatComponent cc)
	{
		super(c, null);
		text = cc;
	}
	
	public IChatComponent getText()
	{ return text; }
	
	@SideOnly(Side.CLIENT)
	public ButtonGuideTextLine createWidget(GuiGuide gui)
	{ return new ButtonGuideExtendedTextLine(gui, this); }
	
	public List<IChatComponent> getHover()
	{ return hover; }
	
	@SideOnly(Side.CLIENT)
	public boolean hasClickAction()
	{ return clickAction != null; }
	
	@SideOnly(Side.CLIENT)
	public void onClicked()
	{
		if(clickAction != null)
		{
			FTBLibClient.playClickSound();
			clickAction.onClicked();
		}
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
		
		return o;
	}
	
	public void setClickAction(ClickAction a)
	{ clickAction = a; }
	
	public void setHover(List<IChatComponent> h)
	{
		if(h == null || h.isEmpty()) hover = null;
		else
		{
			hover = new ArrayList<>(h.size());
			hover.addAll(h);
		}
	}
}