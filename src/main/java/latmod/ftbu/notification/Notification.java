package latmod.ftbu.notification;

import java.lang.reflect.Type;

import com.google.gson.*;

import latmod.lib.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

public class Notification
{
	public final String ID;
	public final IChatComponent title;
	public final int timer;
	public IChatComponent desc = null;
	public int color = 0xFFA0A0A0;
	public ItemStack item = null;
	public ClickAction clickEvent = null;
	
	public Notification(String s, IChatComponent t, int l)
	{
		ID = s;
		title = t;
		timer = MathHelperLM.clampInt(l, 1, 30000);
	}
	
	public void setDesc(IChatComponent c)
	{ desc = c; }
	
	public void setItem(ItemStack is)
	{ item = is; }
	
	public void setColor(int c)
	{ color = c; }
	
	public void setClickEvent(ClickAction e)
	{ clickEvent = e; }
	
	public boolean isTemp()
	{ return clickEvent == null; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || o.toString().equals(ID)); }
	
	public int hashCode()
	{ return ID.hashCode(); }
	
	public String toString()
	{ return ID; }
	
	public static Notification fromJson(String s)
	{ return (Notification)LMJsonUtils.fromJson(s, Notification.class); }
	
	public String toJson()
	{ return LMJsonUtils.toJson(this); }
	
	public static class Serializer implements JsonSerializer<Notification>, JsonDeserializer<Notification>
	{
		public static final Serializer inst = new Serializer();
		
		public JsonElement serialize(Notification n, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject o = new JsonObject();
			o.add("id", new JsonPrimitive(n.ID));
			o.add("title", context.serialize(n.title, IChatComponent.class));
			if(n.timer != 3000) o.add("timer", new JsonPrimitive(n.timer));
			if(n.desc != null) o.add("desc", context.serialize(n.desc, IChatComponent.class));
			if(n.item != null) o.add("item", context.serialize(n.item, ItemStack.class));
			if(n.color != 0xFFA0A0A0) o.add("color", new JsonPrimitive(n.color));
			if(n.clickEvent != null) o.add("click", context.serialize(n.clickEvent));
			return o;
		}
		
		public Notification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if(json.isJsonNull()) return null;
			JsonObject o = json.getAsJsonObject();
			if(!o.has("id") || !o.has("title")) return null;
			IChatComponent t = (IChatComponent)context.deserialize(o.get("title"), IChatComponent.class);
			int l = o.has("timer") ? o.get("timer").getAsInt() : 3000;
			Notification n = new Notification(o.get("id").getAsString(), t, l);
			if(o.has("desc")) n.setDesc((IChatComponent)context.deserialize(o.get("desc"), IChatComponent.class));
			if(o.has("color")) n.setColor(Converter.decodeInt(o.get("color").getAsString()));
			if(o.has("item")) n.setItem((ItemStack)context.deserialize(o.get("item"), ItemStack.class));
			if(o.has("click")) n.setClickEvent((ClickAction)context.deserialize(o.get("click"), ClickAction.class));
			return n;
		}
	}
}