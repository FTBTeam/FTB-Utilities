package latmod.ftbu.core;

import java.lang.reflect.Type;

import latmod.ftbu.core.inv.LMInvUtils;
import latmod.ftbu.core.util.*;
import net.minecraft.item.ItemStack;

import com.google.gson.*;

public class Notification
{
	public final String title;
	public final String desc;
	public final int timer;
	
	public int color = 0xFFA0A0A0;
	public ItemStack item = null;
	private ClickAction action = null;
	
	public Notification(String s, String s1, int t)
	{
		title = (s == null) ? "" : s;
		desc = (s1 == null) ? "" : s1;
		timer = MathHelperLM.clampInt(t, 1, 32000);
	}
	
	public void setItem(ItemStack is)
	{ item = is; }
	
	public void setAction(ClickAction a)
	{ if(a == null || (a.type != null && a.data != null && !a.data.isEmpty())) action = a; }
	
	public void setColor(int c)
	{ color = c; }
	
	public boolean equals(Object o)
	{
		if(o instanceof Notification)
			return equalsNotification((Notification)o);
		return title.equals(o + "");
	}
	
	public boolean equalsNotification(Notification o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(!title.equals(o.title)) return false;
		if(!desc.equals(o.desc)) return false;
		if(!title.equals(o.title)) return false;
		if(color != o.color) return false;
		if(!LatCore.areObjectsEqual(action, o.action, true)) return false;
		return LMInvUtils.itemsEquals(item, o.item, true, true);
	}
	
	public static Notification getFromJson(String s)
	{ return LatCore.fromJson(s, Notification.class); }
	
	public String toString()
	{ return LatCore.toJson(this); }
	
	public ClickAction getAction()
	{ return action; }
	
	public static class ClickAction
	{
		public static enum Type
		{
			LINK("link"),
			COMMAND("cmd");
			
			public final String ID;
			
			Type(String s)
			{ ID = s; }
			
			public static Type fromString(String s)
			{
				for(Type t : values())
					if(t.ID.equals(s)) return t;
				return null;
			}
		}
		
		public final Type type;
		public final String data;
		
		public ClickAction(Type t, String s)
		{ type = t; data = s; }
	}
	
	public static class Serializer implements JsonSerializer<Notification>, JsonDeserializer<Notification>
	{
		public JsonElement serialize(Notification src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject o = new JsonObject();
			
			o.add("title", new JsonPrimitive(src.title));
			if(!src.desc.isEmpty()) o.add("desc", new JsonPrimitive(src.desc));
			if(src.timer != 3000) o.add("timer", new JsonPrimitive(src.timer));
			if(src.color != 0xFFA0A0A0) o.add("color", new JsonPrimitive(src.color));
			if(src.item != null) o.add("item", context.serialize(src.item));
			if(src.action != null)
			{
				JsonObject o1 = new JsonObject();
				o1.add("type", new JsonPrimitive(src.action.type.ID));
				o1.add("data", new JsonPrimitive(src.action.data));
				o.add("action", o1);
			}
			
			return o;
		}
		
		public Notification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if(json.isJsonNull()) return null;
			JsonObject o = json.getAsJsonObject();
			if(!o.has("title")) return null;
			
			String t = o.get("title").getAsString();
			String d = o.has("desc") ? o.get("desc").getAsString() : "";
			int l = o.has("timer") ? o.get("timer").getAsInt() : 3000;
			Notification n = new Notification(t, d, l);
			
			if(o.has("color")) n.color = MathHelperLM.toIntDecoded(o.get("color").getAsString());
			
			if(o.has("item"))
			{
				ItemStack is = context.deserialize(o.get("item"), ItemStack.class);
				if(is != null) n.setItem(is);
			}
			
			if(o.has("action"))
			{
				JsonObject o1 = o.get("").getAsJsonObject();
				if(o1.has("data"))
				{
					ClickAction.Type type = o1.has("type") ? ClickAction.Type.fromString(o1.get("type").getAsString()) : ClickAction.Type.LINK;
					if(type != null) n.setAction(new ClickAction(type, o1.get("data").getAsString()));
				}
			}
			
			return n;
		}
	}
}