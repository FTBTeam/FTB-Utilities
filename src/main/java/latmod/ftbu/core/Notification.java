package latmod.ftbu.core;

import java.lang.reflect.Type;

import com.google.gson.*;

import latmod.ftbu.core.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;

public class Notification
{
	public final String ID;
	public final IChatComponent title;
	public final int timer;
	public IChatComponent desc = null;
	public int color = 0xFFA0A0A0;
	public ItemStack item = null;
	public NotificationClick clickEvent = null;
	
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
	
	public void setClickEvent(NotificationClick e)
	{ clickEvent = e; }
	
	public boolean isTemp()
	{ return clickEvent == null; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || o.toString().equals(ID)); }
	
	public int hashCode()
	{ return String.valueOf(ID).hashCode(); }
	
	public String toString()
	{ return ID; }
	
	public static Notification fromJson(String s)
	{ return (Notification)LMJsonUtils.fromJson(s, Notification.class); }
	
	public String toJson()
	{ return LMJsonUtils.toJson(this); }
	
	public static Notification readFromNBT(NBTTagCompound tag)
	{
		if(tag == null || tag.hasNoTags() || !tag.hasKey("ID") || !tag.hasKey("T"))
			return null;
		
		IChatComponent title = (IChatComponent)LMJsonUtils.fromJson(tag.getString("T"), IChatComponent.class);
		int timer = tag.hasKey("L") ? tag.getInteger("L") : 3000;
		Notification n = new Notification(tag.getString("ID"), title, timer);
		
		if(tag.hasKey("D"))
			n.setDesc((IChatComponent)LMJsonUtils.fromJson(tag.getString("D"), IChatComponent.class));
		
		if(tag.hasKey("I"))
			n.setItem(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("I")));
		
		if(tag.hasKey("C"))
			n.setColor(tag.getInteger("C"));
		
		if(tag.hasKey("CID"))
			n.setClickEvent(new NotificationClick(tag.getString("CID"), tag.getByteArray("CV")));
		
		return n;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("ID", ID);
		tag.setString("T", LMJsonUtils.toJson(title));
		if(timer != 3000) tag.setInteger("L", timer);
		
		if(desc != null) tag.setString("D", LMJsonUtils.toJson(desc));
		
		if(item != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			item.writeToNBT(tag1);
			tag.setTag("I", tag1);
		}
		
		if(color != 0xFFA0A0A0) tag.setInteger("C", color);
		
		if(clickEvent != null)
		{
			tag.setString("CID", clickEvent.ID);
			tag.setByteArray("CV", clickEvent.val);
		}
	}
	
	public static class Serializer implements JsonSerializer<Notification>, JsonDeserializer<Notification>
	{
		public JsonElement serialize(Notification n, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject o = new JsonObject();
			
			o.add("id", new JsonPrimitive(n.ID));
			o.add("title", context.serialize(n.title));
			if(n.timer != 3000) o.add("timer", new JsonPrimitive(n.timer));
			
			if(n.desc != null) o.add("desc", context.serialize(n.desc));
			if(n.item != null) o.add("item", context.serialize(n.item));
			if(n.color != 0xFFA0A0A0) o.add("color", new JsonPrimitive(n.color));
			
			if(n.clickEvent != null)
			{
				JsonObject o1 = new JsonObject();
				o1.add("ID", new JsonPrimitive(n.clickEvent.ID));
				o1.add("val", new JsonPrimitive(new String(n.clickEvent.val)));
				o.add("click", o1);
			}
			
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
			if(o.has("color")) n.setColor(Converter.toIntDecoded(o.get("color").getAsString()));
			if(o.has("item")) n.setItem((ItemStack)context.deserialize(o.get("item"), ItemStack.class));
			if(o.has("click"))
			{
				JsonObject o1 = o.get("click").getAsJsonObject();
				n.setClickEvent(new NotificationClick(o1.get("ID").getAsString(), o1.get("val").getAsString()));
			}
			
			return n;
		}
	}
}