package ftb.utils.api.guide;

import com.google.gson.*;
import latmod.lib.json.IJsonGet;

import java.util.*;

/**
 * Created by LatvianModder on 18.03.2016.
 */
public class GuideInfo implements IJsonGet
{
	public final String name;
	public final GuideType type;
	public final String version;
	public final List<String> authors;
	public final GuideFormat format;
	public final String linked_with;
	public final String file_name;
	public final String version_list_url;
	
	public GuideInfo(JsonObject o)
	{
		name = o.get("name").getAsString();
		type = GuideType.get(o.get("type").getAsString());
		version = o.has("version") ? o.get("version").getAsString() : "1.0.0";
		
		List<String> list = new ArrayList<>();
		
		if(o.has("authors"))
		{
			JsonArray a = o.get("authors").getAsJsonArray();
			
			for(JsonElement e1 : a)
			{
				list.add(e1.getAsString());
			}
		}
		
		authors = Collections.unmodifiableList(list);
		
		format = o.has("format") ? GuideFormat.get(o.get("format").getAsString()) : GuideFormat.TXT;
		linked_with = o.has("linked_with") ? o.get("linked_with").getAsString() : "";
		file_name = o.has("file_name") ? o.get("file_name").getAsString() : name.replace(' ', '-');
		version_list_url = o.has("version_list_url") ? o.get("version_list_url").getAsString() : "";
	}
	
	public JsonElement getJson()
	{
		JsonObject o = new JsonObject();
		
		o.add("name", new JsonPrimitive(name));
		o.add("type", new JsonPrimitive(type.name().toLowerCase()));
		o.add("version", new JsonPrimitive(version));
		
		JsonArray a = new JsonArray();
		
		for(String s : authors)
		{
			a.add(new JsonPrimitive(s));
		}
		
		o.add("authors", a);
		
		o.add("format", new JsonPrimitive(format.name().toLowerCase()));
		o.add("linked_with", new JsonPrimitive(linked_with));
		o.add("file_name", new JsonPrimitive(file_name));
		o.add("version_list_url", new JsonPrimitive(version_list_url));
		
		return o;
	}
}