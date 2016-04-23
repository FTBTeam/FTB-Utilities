package ftb.utils.api.guide.repos;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public final class GuideInfo
{
	public final String name;
	public final GuideType type;
	public final String version;
	public final List<String> authors;
	public final List<String> modes;
	
	public GuideInfo(JsonObject o)
	{
		name = o.get("name").getAsString();
		type = o.has("type") ? GuideType.getFromID(o.get("type").getAsString()) : GuideType.CUSTOM;
		version = o.has("version") ? o.get("version").getAsString() : "0.0.0";
		
		List<String> list = new ArrayList<>();
		
		if(o.has("authors"))
		{
			for(JsonElement e : o.get("authors").getAsJsonArray())
			{
				list.add(e.getAsString());
			}
		}
		
		authors = Collections.unmodifiableList(list);
		
		list = new ArrayList<>();
		
		if(o.has("modes"))
		{
			for(JsonElement e : o.get("modes").getAsJsonArray())
			{
				list.add(e.getAsString());
			}
		}
		
		if(!list.contains("common")) list.add("common");
		
		modes = Collections.unmodifiableList(list);
	}
}