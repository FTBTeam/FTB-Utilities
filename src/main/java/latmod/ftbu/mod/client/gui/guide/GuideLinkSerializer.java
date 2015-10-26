package latmod.ftbu.mod.client.gui.guide;

import java.lang.reflect.Type;

import com.google.gson.*;

import latmod.ftbu.api.guide.GuideLink;

public class GuideLinkSerializer implements JsonDeserializer<GuideLink>
{
	public static final Gson gson = createGson();
	
	private static final Gson createGson()
	{
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(GuideLink.class, new GuideLinkSerializer());
		return gb.create();
	}
	
	public GuideLink deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(json.isJsonNull() || !json.isJsonObject()) return null;
		
		JsonObject o = json.getAsJsonObject();
		
		if(o.has("type"))
		{
			String type = o.get("type").getAsString();
			
			GuideLink special = null;
			
			if(type.equals("url"))
				special = new GuideLink(GuideLink.TYPE_URL);
			else if(type.equals("image"))
				special = new GuideLink(GuideLink.TYPE_IMAGE);
			else if(type.equals("image_url"))
				special = new GuideLink(GuideLink.TYPE_IMAGE_URL);
			
			if(special != null && o.has("link"))
			{
				special.link = o.get("link").getAsString();
				if(o.has("title")) special.text = o.get("title").getAsString(); else special.text = special.link;
				if(o.has("hover")) special.hover = o.get("hover").getAsString();
				
				return special;
			}
		}
		
		return null;
	}
}