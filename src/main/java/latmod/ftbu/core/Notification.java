package latmod.ftbu.core;

import latmod.ftbu.core.inv.LMInvUtils;
import latmod.ftbu.core.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.annotations.Expose;

public class Notification
{
	public final String title;
	public final String desc;
	public final int timer;
	
	public ItemStack item = null;
	public ClickAction action = null;
	public int color = 0xFFA0A0A0;
	
	public Notification(String s, String s1, int t)
	{
		title = (s == null) ? "" : s;
		desc = (s1 == null) ? "" : s1;
		timer = MathHelperLM.clampInt(t, 1, 32000);
	}
	
	public void setItem(ItemStack is)
	{ item = is; }
	
	public void setAction(ClickAction a)
	{ action = a; }
	
	public void setColor(int c)
	{ color = c; }
	
	public boolean equals(Object o)
	{
		if(o instanceof Notification)
			return equalsNotification((Notification)o);
		return title.equals(o + "");
	}
	
	public boolean equalsNotification(Notification o)
	{ return title.equals(o.title) && desc.equals(o.desc) && LMInvUtils.itemsEquals(item, o.item, true, true); }
	
	public static Notification getFromJson(String s)
	{
		JsonLoader j = LatCore.fromJson(s, JsonLoader.class);
		
		if(j != null && j.title != null)
		{
			if(j.timer == null) j.timer = 3000;
			Notification n = new Notification(j.title, j.desc, j.timer);
			
			if(j.item != null) n.setItem(j.item);
			if(j.action != null && j.action.data != null) n.setAction(j.action);
			if(j.color != null) n.setColor(MathHelperLM.toIntDecoded(j.color));
			return n;
		}
		
		return null;
	}
	
	public static Notification readFromNBT(NBTTagCompound tag)
	{
		if(!tag.hasKey("T")) return null;
		String t = tag.getString("T");
		String d = tag.getString("D");
		int l = tag.getShort("L");
		Notification n = new Notification(t, d, l);
		
		if(tag.hasKey("I"))
			n.setItem(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("I")));
		
		if(tag.hasKey("A"))
			n.setAction(new ClickAction(tag.getByte("A"), tag.getString("AD")));
		
		if(tag.hasKey("C"))
			n.color = tag.getInteger("C");
		
		return n;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("T", title);
		if(!desc.isEmpty()) tag.setString("D", desc);
		tag.setShort("L", (short)timer);
		
		if(item != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			item.writeToNBT(tag1);
			tag.setTag("I", tag1);
		}
		
		if(action != null)
		{
			tag.setByte("A", (byte)action.id);
			tag.setString("AD", action.data);
		}
		
		if(color != 0xFFA0A0A0)
			tag.setInteger("C", color);
	}
	
	public static class ClickAction
	{
		public static final int LINK = 0;
		public static final int COMMAND = 1;
		
		@Expose public int id;
		@Expose public String data;
		
		public ClickAction() { }
		
		public ClickAction(byte b, String s)
		{ id = b; data = s; }
	}
	
	private static class JsonLoader
	{
		@Expose public String title;
		@Expose public String desc;
		@Expose public Integer timer;
		@Expose public String color;
		@Expose public ItemStack item;
		@Expose public ClickAction action;
	}
}