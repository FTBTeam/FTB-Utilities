package mods.lm_core;
import mods.lm_core.mod.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;

public class LMSecurity
{
	public static final int PUBLIC = 0;
	public static final int PRIVATE = 1;
	public static final int RESTRICTED = 2;
	
	public String owner = null;
	public int level = PUBLIC;
	public String[] friends = new String[0];
	
	public LMSecurity(String s)
	{ owner = s; }
	
	public LMSecurity(EntityPlayer ep)
	{ this(ep == null ? null : ep.username); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		owner = tag.getString("Owner");
		level = tag.getByte("Level");
		friends = new String[0];
		
		if(tag.hasKey("Friends"))
		{
			NBTTagList list = tag.getTagList("Friends");
			friends = new String[list.tagCount()];
			for(int i = 0; i < friends.length; i++)
			friends[i] = ((NBTTagString)list.tagAt(i)).data;
		}
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("Owner", owner);
		tag.setByte("Level", (byte)level);
		
		if(friends.length > 0)
		{
			NBTTagList list = new NBTTagList();
			for(int i = 0; i < friends.length; i++)
			list.appendTag(new NBTTagString(friends[i]));
			tag.setTag("Friends", list);
		}
	}
	
	public boolean canPlayerInteract(String name)
	{
		if(level == PUBLIC) return true;
		if(name == null || name.length() == 0) return false;
		if(owner.equals(name)) return true;
		
		if(level == RESTRICTED)
		{
			for(int i = 0; i < friends.length; i++)
			if(friends[i].equals(name)) return true;
		}
		
		return false;
	}
	
	public boolean canPlayerInteract(EntityPlayer ep)
	{ return canPlayerInteract(ep == null ? null : ep.username); }
}