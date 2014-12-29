package latmod.core;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Notification
{
	public final String title;
	public final String desc;
	public final ItemStack item;
	public final long timer;
	
	public Notification(String s, String s1, ItemStack is, long t)
	{
		title = (s == null) ? "" : s;
		desc = (s1 == null) ? "" : s1;
		item = (is == null) ? new ItemStack(Blocks.stone) : is;
		timer = Math.max(1, t);
	}
	
	public Notification(String s, String s1, ItemStack is)
	{ this(s, s1, is, 3000L); }
	
	public boolean equals(Object o)
	{
		if(o instanceof Notification)
			return equalsNotification((Notification)o);
		return title.equals(o + "");
	}

	public boolean equalsNotification(Notification o)
	{ return title.equals(o.title) && desc.equals(o.desc) && InvUtils.itemsEquals(item, o.item, false, true); }
	
	public static Notification readFromNBT(NBTTagCompound tag)
	{ return new Notification(tag.getString("T"), tag.getString("D"), ItemStack.loadItemStackFromNBT(tag.getCompoundTag("I")), tag.getLong("S")); }
	
	public void writeToNBT(NBTTagCompound tag)
	{
		if(!title.isEmpty()) tag.setString("T", title);
		if(!desc.isEmpty()) tag.setString("D", desc);
		NBTTagCompound tag1 = new NBTTagCompound();
		item.writeToNBT(tag1);
		tag.setTag("I", tag1);
		tag.setLong("S", timer);
	}
}