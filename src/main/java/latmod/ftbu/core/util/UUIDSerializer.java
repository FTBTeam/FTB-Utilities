package latmod.ftbu.core.util;

import java.lang.reflect.Type;
import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;

import com.google.gson.*;

public class UUIDSerializer implements JsonSerializer<UUID>, JsonDeserializer<UUID>
{
	public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{ return LatCoreMC.getUUIDFromString(json.getAsString()); }
	
	public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context)
	{ return new JsonPrimitive(LatCoreMC.toShortUUID(src)); }
}