package latmod.ftbu.core;

import java.lang.reflect.Type;

import latmod.ftbu.core.util.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;

import com.google.gson.*;

public class Notification
{
	public final String ID;
	public final IChatComponent title;
	public final int timer;
	
	private IChatComponent desc = null;
	private int color = 0xFFA0A0A0;
	private ItemStack item = null;
	private ClickEvent clickEvent = null;
	
	public Notification(String s, IChatComponent t, int l)
	{
		ID = s;
		title = t;
		timer = MathHelperLM.clampInt(l, 1, 30000);
	}
	
	public void setDesc(IChatComponent c)
	{ desc = c; }
	
	public IChatComponent getDesc()
	{ return desc; }
	
	public void setItem(ItemStack is)
	{ item = is; }
	
	public ItemStack getItem()
	{ return item; }
	
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
	{ return o != null && (o == this || o.toString().equals(toString())); }
	
	public int hashCode()
	{ return String.valueOf(ID).hashCode(); }
	
	public String toString()
	{ return ID; }
	
	public static Notification fromJson(String s)
	{ return LMJsonUtils.fromJson(s, Notification.class); }
	
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
		
		if(tag.hasKey("CEA"))
			n.setClickEvent(new ClickEvent(ClickEvent.Action.values()[tag.getByte("CEA")], tag.getString("CEV")));
		
		return n;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("ID", ID);
		tag.setString("T", LMJsonUtils.toJson(title));
		if(timer != 3000) tag.setInteger("L", timer);
		
		IChatComponent desc = getDesc();
		if(desc != null) tag.setString("D", LMJsonUtils.toJson(desc));
		
		ItemStack is = getItem();
		if(is != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			is.writeToNBT(tag1);
			tag.setTag("I", tag1);
		}
		
		int col = getColor();
		if(col != 0xFFA0A0A0) tag.setInteger("C", col);
		
		ClickEvent ce = getClickEvent();
		if(ce != null)
		{
			tag.setByte("CEA", (byte)ce.getAction().ordinal());
			tag.setString("CEV", ce.getValue());
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
			
			IChatComponent desc = n.getDesc();
			if(desc != null) o.add("desc", context.serialize(desc));
			
			ItemStack is = n.getItem();
			if(is != null) o.add("item", context.serialize(is));
			
			int col = n.getColor();
			if(col != 0xFFA0A0A0) o.add("color", new JsonPrimitive(col));
			
			ClickEvent ce = n.getClickEvent();
			if(ce != null)
			{
				JsonObject o1 = new JsonObject();
				o1.add("action", new JsonPrimitive(ce.getAction().getCanonicalName()));
				o1.add("value", new JsonPrimitive(ce.getValue()));
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