package ftb.utils.api.guide.repos;

import com.google.gson.*;
import ftb.lib.JsonHelper;
import ftb.lib.api.info.InfoPage;
import latmod.lib.util.FinalIDObject;

import java.util.Map;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideMode extends FinalIDObject
{
	public final GuideRepo guide;
	private InfoPage infoPage;
	
	public GuideMode(GuideRepo g, String id) throws Exception
	{
		super(id);
		guide = g;
	}
	
	public InfoPage getInfoPage()
	{
		if(infoPage != null) return infoPage;
		
		try
		{
			infoPage = getPage(null, getID(), guide.getFile("guide/" + getID() + "/pages.json").asJson());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			infoPage = new InfoPage(getID());
		}
		
		return infoPage;
	}
	
	private InfoPage getPage(InfoPage parent, String id, JsonElement e) throws Exception
	{
		InfoPage page = new InfoPage(id);
		page.setParent(parent);
		
		if(e.isJsonPrimitive())
		{
			page.setTitle(JsonHelper.deserializeICC(e));
		}
		else if(e.isJsonObject())
		{
			JsonObject o = new JsonObject();
			
			if(o.has("name"))
			{
				page.setTitle(JsonHelper.deserializeICC(o.get("name")));
			}
			
			if(o.has("page"))
			{
				String path = o.get("page").getAsString();
				page.printlnText("Hello!");
			}
			
			if(o.has("pages"))
			{
				for(Map.Entry<String, JsonElement> e1 : o.get("pages").getAsJsonObject().entrySet())
				{
					page.addSub(getPage(page, e1.getKey(), e1.getValue()));
				}
			}
		}
		
		return page;
	}
}