package latmod.ftbu.api.config;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.*;

import latmod.core.util.FastList;

public class ConfigList
{
	public FastList<ConfigGroup> list;
	
	public static class Serializer implements JsonSerializer<ConfigList>, JsonDeserializer<ConfigList>
	{
		public JsonElement serialize(ConfigList src, Type typeOfSrc, JsonSerializationContext context)
		{
			if(src == null) return null;
			
			JsonObject o = new JsonObject();
			
			for(ConfigGroup g : src.list)
			{
				JsonObject o1 = new JsonObject();
				
				for(ConfigEntry e : g.config)
					o1.add(e.ID, context.serialize(e.getJson()));
				
				o.add(g.ID, o1);
			}
			
			return o;
		}
		
		public ConfigList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if(json.isJsonNull()) return null;
			ConfigList c = new ConfigList();
			c.list = new FastList<ConfigGroup>();
			
			JsonObject o = json.getAsJsonObject();
			
			for(Map.Entry<String, JsonElement> e : o.entrySet())
			{
				ConfigGroup g = ConfigFileRegistry.getGroup(e.getKey());
				
				if(g != null && g.parentFile.canEdit)
				{
					JsonObject o1 = e.getValue().getAsJsonObject();
					
					for(Map.Entry<String, JsonElement> e1 : o1.entrySet())
					{
						ConfigEntry entry = g.config.getObj(e1.getKey());
						if(entry != null) entry.setJson(e1.getValue().isJsonNull() ? null : context.deserialize(e1.getValue(), entry.type.typeClass));
					}
					
					c.list.add(g);
				}
			}
			
			return c;
		}
	}
}