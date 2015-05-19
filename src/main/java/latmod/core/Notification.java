package latmod.core;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Notification
{
	public final String title;
	public final String desc;
	public final ItemStack item;
	public final int timer;
	
	public Notification(String s, String s1, ItemStack is, int t)
	{
		title = (s == null) ? "" : s;
		desc = (s1 == null) ? "" : s1;
		item = is;
		timer = Math.max(1, t);
	}
	
	public Notification(String s, String s1, ItemStack is)
	{ this(s, s1, is, 3000); }
	
	public boolean equals(Object o)
	{
		if(o instanceof Notification)
			return equalsNotification((Notification)o);
		return title.equals(o + "");
	}
	
	public boolean equalsNotification(Notification o)
	{ return title.equals(o.title) && desc.equals(o.desc) && InvUtils.itemsEquals(item, o.item, true, true); }
	
	public static Notification readFromNBT(NBTTagCompound tag)
	{ return new Notification(tag.getString("T"), tag.getString("D"), ItemStack.loadItemStackFromNBT(tag.getCompoundTag("I")), tag.getInteger("R")); }
	
	public void writeToNBT(NBTTagCompound tag)
	{
		if(!title.isEmpty()) tag.setString("T", title);
		if(!desc.isEmpty()) tag.setString("D", desc);
		
		if(item != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			item.writeToNBT(tag1);
			tag.setTag("I", tag1);
		}
		
		tag.setInteger("R", timer);
	}
}