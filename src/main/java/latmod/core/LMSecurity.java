package latmod.core;
import latmod.core.mod.LC;
import latmod.core.util.FastList;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;

public class LMSecurity
{
	public static enum Level
	{
		PUBLIC("public"),
		PRIVATE("private"),
		WHITELIST("whitelist"),
		BLACKLIST("blacklist");
		
		public static final Level[] VALUES = values();
		
		public final int ID;
		private String uname;
		
		Level(String s)
		{
			ID = ordinal();
			uname = s;
		}
		
		public boolean isRestricted()
		{ return this == WHITELIST || this == BLACKLIST; }
		
		public Level next()
		{ return VALUES[(ID + 1) % VALUES.length]; }
		
		public Level prev()
		{
			int id = ID - 1;
			if(id < 0) id = VALUES.length - 1;
			return VALUES[id];
		}
		
		public String getText()
		{ return LC.mod.translate("security." + uname); }
		
		public String getTitle()
		{ return LC.mod.translate("security"); }
	}
	
	public String owner = null;
	public Level level = Level.PUBLIC;
	public FastList<String> restricted;
	
	public LMSecurity(String s)
	{
		owner = s;
		restricted = new FastList<String>();
	}
	
	public LMSecurity(EntityPlayer ep)
	{ this(ep == null ? null : ep.getCommandSenderName()); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		owner = tag.getString("Owner");
		if(owner.length() == 0) owner = null;
		
		level = Level.VALUES[tag.getByte("Level")];
		restricted.clear();
		
		if(tag.hasKey("Restricted"))
		{
			NBTTagList list = tag.getTagList("Restricted", LatCore.NBT_STRING);
			for(int i = 0; i < list.tagCount(); i++)
			restricted.add(list.getStringTagAt(i));
		}
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("Owner", (owner == null) ? "" : owner);
		tag.setByte("Level", (byte)level.ID);
		
		if(!restricted.isEmpty())
		{
			NBTTagList list = new NBTTagList();
			for(String s : restricted)
			list.appendTag(new NBTTagString(s));
			tag.setTag("Restricted", list);
		}
	}
	
	public boolean canInteract(String name)
	{
		if(level == Level.PUBLIC) return true;
		if(name == null || name.length() == 0) return false;
		if(isOwner(name)) return true;
		if(level == Level.PRIVATE) return false;
		
		else if(level == Level.WHITELIST)
		{
			if(restricted.isEmpty()) return false;
			else
			{
				for(String s : restricted)
				{
					if(s.equalsIgnoreCase(name))
					return true;
				}
				
				return false;
			}
		}
		
		else if(level == Level.BLACKLIST)
		{
			if(restricted.isEmpty()) return true;
			else
			{
				for(String s : restricted)
				{
					if(s.equalsIgnoreCase(name))
					return false;
				}
				
				return false;
			}
		}
		
		else return false;
	}
	
	public boolean canPlayerInteract(EntityPlayer ep)
	{ return canInteract(ep == null ? null : ep.getCommandSenderName()); }
	
	public boolean isOwner(String name)
	{ return owner == null || (name != null && owner.equalsIgnoreCase(name)); }
	
	public boolean isPlayerOwner(EntityPlayer ep)
	{ return isOwner(ep == null ? null : ep.getCommandSenderName()); }
}