package latmod.ftbu.core;

import latmod.ftbu.core.inv.InvUtils;
import latmod.ftbu.core.util.MathHelperLM;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Notification
{
	public final String title;
	public final String desc;
	public final int timer;
	
	public ItemStack item = null;
	public ClickAction action = null;
	
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
	
	public boolean equals(Object o)
	{
		if(o instanceof Notification)
			return equalsNotification((Notification)o);
		return title.equals(o + "");
	}
	
	public boolean equalsNotification(Notification o)
	{ return title.equals(o.title) && desc.equals(o.desc) && InvUtils.itemsEquals(item, o.item, true, true); }
	
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
			tag.setByte("A", action.ID);
			tag.setString("AD", action.data);
		}
	}
	
	public static class ClickAction
	{
		public static final byte LINK = 0;
		public static final byte COMMAND = 1;
		
		public final byte ID;
		public final String data;
		
		public ClickAction(byte b, String s)
		{ ID = b; data = s; }
	}
}