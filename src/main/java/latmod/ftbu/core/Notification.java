package latmod.ftbu.core;

import java.lang.reflect.Type;

import latmod.ftbu.core.inv.LMInvUtils;
import latmod.ftbu.core.util.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

import com.google.gson.*;

public class Notification
{
	public final IChatComponent title;
	public final int timer;
	
	public IChatComponent desc = null;
	private int color = 0xFFA0A0A0;
	public ItemStack item = null;
	private ClickEvent clickEvent = null;
	
	public Notification(IChatComponent t, int l)
	{
		title = t;
		timer = MathHelperLM.clampInt(l, 1, 30000);
	}
	
	public void setDesc(IChatComponent c)
	{ desc = c; }
	
	public void setItem(ItemStack is)
	{ item = is; }
	
	public void setColor(int c)
	{ color = c; }
	
	public int getColor()
	{ return color; }
	
	public void setClickEvent(ClickEvent e)
	{ clickEvent = e; }
	
	public ClickEvent getClickEvent()
	{ return clickEvent; }
	
	public boolean isTemp()
	{ return clickEvent == null; }
	
	public boolean equals(Object o)
	{ return o != null && (o instanceof Notification) && equalsNotification((Notification)o); }
	
	public boolean equalsNotification(Notification o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(!title.equals(o.title)) return false;
		if(LatCore.areObjectsEqual(desc, o.desc, true)) return false;
		if(color != o.color) return false;
		return LMInvUtils.itemsEquals(item, o.item, true, true);
	}
	
	public static Notification getFromJson(String s)
	{ return LatCore.fromJson(s, Notification.class); }
	
	public String toString()
	{ return LatCore.toJson(this); }
	
	public static class Serializer implements JsonSerializer<Notification>, JsonDeserializer<Notification>
	{
		public JsonElement serialize(Notification n, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject o = new JsonObject();
			
			o.add("title", context.serialize(n.title));
			if(n.timer != 3000) o.add("timer", new JsonPrimitive(n.timer));
			if(n.desc != null) o.add("desc", context.serialize(n.desc));
			if(n.item != null) o.add("item", context.serialize(n.item));
			int col = n.getColor();
			if(col != 0xFFA0A0A0) o.add("color", new JsonPrimitive(col));
			if(n.clickEvent != null)
			{
				JsonObject o1 = new JsonObject();
				o1.add("action", new JsonPrimitive(n.clickEvent.getAction().getCanonicalName()));
				o1.add("value", new JsonPrimitive(n.clickEvent.getValue()));
				o.add("click", o1);
			}
			
			return o;
		}
		
		public Notification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if(json.isJsonNull()) return null;
			JsonObject o = json.getAsJsonObject();
			if(!o.has("title")) return null;
			
			IChatComponent t = (IChatComponent)context.deserialize(o.get("title"), IChatComponent.class);
			int l = o.has("timer") ? o.get("timer").getAsInt() : 3000;
			Notification n = new Notification(t, l);
			
			if(o.has("desc")) n.setDesc((IChatComponent)context.deserialize(o.get("desc"), IChatComponent.class));
			if(o.has("color")) n.setColor(MathHelperLM.toIntDecoded(o.get("color").getAsString()));
			if(o.has("item")) n.setItem((ItemStack)context.deserialize(o.get("item"), ItemStack.class));
			if(o.has("click"))
			{
				JsonObject o1 = o.get("click").getAsJsonObject();
				ClickEvent.Action a = ClickEvent.Action.getValueByCanonicalName(o1.get("action").getAsString());
				if(a != null) n.setClickEvent(new ClickEvent(a, o1.get("value").getAsString()));
			}
			
			return n;
		}
	}
}