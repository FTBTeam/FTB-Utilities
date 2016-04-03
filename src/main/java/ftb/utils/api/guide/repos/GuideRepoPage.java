package ftb.utils.api.guide.repos;

import com.google.gson.*;
import ftb.lib.JsonHelper;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.*;

import java.util.*;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideRepoPage extends FinalIDObject
{
	public final IChatComponent name;
	public final String page;
	public final Map<String, GuideRepoPage> pages;
	
	public GuideRepoPage(String s, JsonElement e)
	{
		super(s);
		
		Map<String, GuideRepoPage> map = new LinkedHashMap<>();
		
		if(e.isJsonPrimitive())
		{
			name = JsonHelper.deserializeICC(e);
			page = "";
		}
		else
		{
			JsonObject o = e.getAsJsonObject();
			name = o.has("name") ? JsonHelper.deserializeICC(o.get("name")) : new ChatComponentText(s);
			page = o.has("page") ? o.get("page").getAsString() : "";
			
			if(o.has("pages"))
			{
				for(Map.Entry<String, JsonElement> entry : o.get("pages").getAsJsonObject().entrySet())
				{
					GuideRepoPage p = new GuideRepoPage(entry.getKey(), entry.getValue());
					map.put(p.getID(), p);
				}
			}
		}
		
		pages = Collections.unmodifiableMap(map);
	}
	
	public JsonElement getJson()
	{
		if(page.isEmpty() && pages.isEmpty())
		{
			return JsonHelper.serializeICC(name);
		}
		
		JsonObject o = new JsonObject();
		o.add("name", JsonHelper.serializeICC(name));
		if(!page.isEmpty()) o.add("page", new JsonPrimitive(page));
		if(!pages.isEmpty())
		{
			JsonObject o1 = new JsonObject();
			
			for(GuideRepoPage p : pages.values())
			{
				o1.add(p.getID(), p.getJson());
			}
			
			o.add("pages", o1);
		}
		return o;
	}
}
