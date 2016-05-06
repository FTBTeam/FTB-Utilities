package ftb.utils.api.guide;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import latmod.lib.util.FinalIDObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LatvianModder on 06.05.2016.
 */
public class OnlineGuideInfo extends FinalIDObject
{
	public final String name;
	public final String version;
	public final String logo;
	public final String guide_url;
	public final String base_url;
	public final List<String> authors;
	
	public OnlineGuideInfo(String id, JsonObject o)
	{
		super(id);
		name = o.get("name").getAsString();
		version = o.has("version") ? o.get("version").getAsString() : "1.0.0";
		logo = o.has("logo") ? o.get("logo").getAsString() : "";
		guide_url = o.get("guide_url").getAsString();
		base_url = o.has("base_url") ? o.get("base_url").getAsString() : "";
		
		List<String> l = new ArrayList<>();
		
		if(o.has("authors"))
		{
			for(JsonElement e : o.get("authors").getAsJsonArray())
			{
				l.add(e.getAsString());
			}
		}
		
		authors = Collections.unmodifiableList(l);
	}
}