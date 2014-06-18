package latmod.core;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;

public class LMSecurity
{
	public static final int PUBLIC = 0;
	public static final int PRIVATE = 1;
	public static final int RESTRICTED = 2;
	
	public String owner = null;
	public int level = PUBLIC;
	public FastList<String> friends = new FastList<String>();
	
	public LMSecurity(String s)
	{ owner = s; }
	
	public LMSecurity(EntityPlayer ep)
	{ this(ep == null ? null : ep.getCommandSenderName()); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		owner = tag.getString("Owner");
		if(owner.length() == 0) owner = null;
		
		level = tag.getByte("Level");
		friends.clear();
		
		if(tag.hasKey("Friends"))
		{
			NBTTagList list = tag.getTagList("Friends", LatCore.NBT_STRING);
			for(int i = 0; i < list.tagCount(); i++)
			friends.add(list.getStringTagAt(i));
		}
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("Owner", (owner == null) ? "" : owner);
		tag.setByte("Level", (byte)level);
		
		if(!friends.isEmpty())
		{
			NBTTagList list = new NBTTagList();
			for(String s : friends)
			list.appendTag(new NBTTagString(s));
			tag.setTag("Friends", list);
		}
	}
	
	public boolean canInteract(String name)
	{
		if(level == PUBLIC) return true;
		if(name == null || name.length() == 0) return false;
		if(owner == null || owner.equals(name)) return true;
		if(level == RESTRICTED)
		return friends.contains(name);
		return false;
	}
	
	public boolean canPlayerInteract(EntityPlayer ep)
	{ return canInteract(ep == null ? null : ep.getCommandSenderName()); }
}