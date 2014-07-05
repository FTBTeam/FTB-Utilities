package latmod.core;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;

public class LMSecurity
{
	public static final int PUBLIC = 0;
	public static final int PRIVATE = 1;
	public static final int WHITELIST = 2;
	public static final int BLACKLIST = 3;
	
	public String owner = null;
	public int level = PUBLIC;
	public FastList<String> restricted = new FastList<String>();
	
	public LMSecurity(String s)
	{ owner = s; }
	
	public LMSecurity(EntityPlayer ep)
	{ this(ep == null ? null : ep.getCommandSenderName()); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		owner = tag.getString("Owner");
		if(owner.length() == 0) owner = null;
		
		level = tag.getByte("Level");
		restricted.clear();
		
		if(tag.hasKey("Restricted"))
		{
			NBTTagList list = tag.getTagList("Restricted");
			for(int i = 0; i < list.tagCount(); i++)
			restricted.add(((NBTTagString)list.tagAt(i)).data);
		}
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("Owner", (owner == null) ? "" : owner);
		tag.setByte("Level", (byte)level);
		
		if(!restricted.isEmpty())
		{
			NBTTagList list = new NBTTagList();
			for(String s : restricted)
			list.appendTag(new NBTTagString(null, s));
			tag.setTag("Restricted", list);
		}
	}
	
	public boolean canInteract(String name)
	{
		if(level == PUBLIC) return true;
		if(name == null || name.length() == 0) return false;
		if(isOwner(name)) return true;
		if(level == PRIVATE) return false;
		
		else if(level == WHITELIST)
		for(String s : restricted)
		{
			if(s.equalsIgnoreCase(name))
			return true;
		}
		
		else if(level == BLACKLIST)
		for(String s : restricted)
		{
			if(s.equalsIgnoreCase(name))
			return false;
		}
		
		return true;
	}
	
	public boolean canPlayerInteract(EntityPlayer ep)
	{ return canInteract(ep == null ? null : ep.getCommandSenderName()); }
	
	public boolean isOwner(String name)
	{ return owner == null || (name != null && owner.equalsIgnoreCase(name)); }
	
	public boolean isPlayerOwner(EntityPlayer ep)
	{ return isOwner(ep == null ? null : ep.username); }
	
	public boolean isLevelRestricted()
	{ return level == WHITELIST || level == BLACKLIST; }
}