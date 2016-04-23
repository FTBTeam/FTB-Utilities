package ftb.utils.api.guide.repos;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ftb.lib.JsonHelper;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideRepoPage extends FinalIDObject
{
	public final GuideRepoPage parent;
	public final IChatComponent name;
	public final String page;
	public final Map<String, GuideRepoPage> pages;
	
	public GuideRepoPage(GuideRepoPage p, String s, JsonElement e)
	{
		super(s);
		parent = p;
		
		Map<String, GuideRepoPage> map = new LinkedHashMap<>();
		
		if(e.isJsonPrimitive())
		{
			name = JsonHelper.deserializeICC(e);
			page = "";
		}
		else
		{
			JsonObject o = e.getAsJsonObject();
			name = o.has("name") ? JsonHelper.deserializeICC(o.get("name")) : null;
			page = o.has("page") ? o.get("page").getAsString() : "";
			
			if(o.has("pages"))
			{
				for(Map.Entry<String, JsonElement> entry : o.get("pages").getAsJsonObject().entrySet())
				{
					GuideRepoPage p1 = new GuideRepoPage(this, entry.getKey(), entry.getValue());
					map.put(p1.getID(), p1);
				}
			}
		}
		
		pages = Collections.unmodifiableMap(map);
	}
	
	public JsonElement getJson()
	{
		if(page.isEmpty() && pages.isEmpty())
		{
			return JsonHelper.serializeICC(getName());
		}
		
		JsonObject o = new JsonObject();
		if(name != null) o.add("name", JsonHelper.serializeICC(name));
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
	
	public GuideRepoPage copy()
	{ return new GuideRepoPage(parent == null ? null : parent.copy(), getID(), getJson()); }
	
	public String getPath()
	{ return (parent == null) ? getID() : (parent.getPath() + '/' + getID()); }
	
	public String getPagePath()
	{
		if(page.isEmpty()) return page;
		else if(page.charAt(0) == '/') return page.substring(1);
		else return getPath() + '/' + page;
	}
	
	public IChatComponent getName()
	{
		if(name != null) return name;
		return new ChatComponentText(getID());
	}
	
	
}