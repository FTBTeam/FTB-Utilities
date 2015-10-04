package latmod.ftbu.notification;

import java.lang.reflect.Type;

import com.google.gson.*;

import latmod.core.util.PrimitiveType;

public class ClickAction
{
	public static final ClickActionType CMD = new ClickActionType("cmd", PrimitiveType.STRING);
	public static final ClickActionType SHOW_CMD = new ClickActionType("show_cmd", PrimitiveType.STRING);
	public static final ClickActionType URL = new ClickActionType("url", PrimitiveType.STRING);
	public static final ClickActionType FILE = new ClickActionType("file", PrimitiveType.STRING);
	public static final ClickActionType GUI = new ClickActionType("gui", PrimitiveType.STRING);
	public static final ClickActionType FRIEND_ADD = new ClickActionType("friend_add", PrimitiveType.INT);
	public static final ClickActionType FRIEND_ADD_ALL = new ClickActionType("friend_add", PrimitiveType.NULL);
	
	public final ClickActionType ID;
	public final Object val;
	
	public ClickAction(ClickActionType t, Object v)
	{ ID = t; val = v; }
		
	public String stringVal()
	{ return val.toString(); }
	
	public int intVal()
	{ return val.hashCode(); }
	
	public Number numVal()
	{ return (Number)val; }
	
	public boolean boolVal()
	{ return ((Boolean)val).booleanValue(); }
	
	public static class Serializer implements JsonSerializer<ClickAction>, JsonDeserializer<ClickAction>
	{
		public JsonElement serialize(ClickAction src, Type typeOfSrc, JsonSerializationContext context)
		{
			if(src == null) return null;
			JsonObject o = new JsonObject();
			o.add("ID", new JsonPrimitive(src.ID.ID));
			o.add("type", new JsonPrimitive(src.ID.type.ID));
			if(!PrimitiveType.isNull(src.ID.type))
				o.add("val", context.serialize(src.val));
			return o;
		}
		
		public ClickAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if(json.isJsonNull()) return null;
			JsonObject o1 = json.getAsJsonObject();
			String id = o1.get("ID").getAsString();
			PrimitiveType type = PrimitiveType.get(o1.get("type").getAsString());
			Object val = PrimitiveType.isNull(type) ? null : context.deserialize(o1.get("val"), type.typeClass);
			return new ClickAction(new ClickActionType(id, type), val);
		}
	}
}