package latmod.core.security;

import java.util.*;

import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LMSecurity
{
	public UUID owner;
	public Level level;
	
	public LMSecurity(UUID id)
	{
		owner = id;
		if(owner == null)
			owner = UUID.randomUUID();
		level = Level.PUBLIC;
	}
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		owner = UUID.fromString(tag.getString("Sec_" + s + "_Owner"));
		level = Level.VALUES[tag.getByte("Sec_" + s + "_Level")];
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		tag.setString("Sec_" + s + "_Owner", owner.toString());
		tag.setByte("Sec_" + s + "_Level", (byte)level.ID);
	}
	
	public boolean isOwner(UUID id)
	{ return owner.equals(id); }
	
	public boolean isOwner(EntityPlayer ep)
	{ return isOwner((ep == null) ? null : ep.getUniqueID()); }
	
	public boolean canInteract(UUID id)
	{
		if(level == Level.PUBLIC) return true;
		if(id == null) return false;
		if(isOwner(id)) return true;
		if(level == Level.PRIVATE) return false;
		
		JsonPlayer player = getPlayer(id);
		
		if(player != null)
		{
			String idS = id.toString();
			
			if(level == Level.WHITELIST)
			{
				List<String> l = player.whitelist;
				
				if(l != null && l.size() > 0)
				{
					for(int i = 0; i < l.size(); i++)
					{
						if(l.equals(idS))
							return true;
					}
					
					return false;
				}
			}
			
			if(level == Level.BLACKLIST)
			{
				List<String> l = player.blacklist;
				
				if(l != null && l.size() > 0)
				{
					for(int i = 0; i < l.size(); i++)
					{
						if(l.equals(idS))
							return false;
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean canInteract(EntityPlayer ep)
	{ return canInteract((ep == null) ? null : ep.getUniqueID()); }
	
	// Static methods //
	
	public static JsonPlayerList list;
	
	public static JsonPlayer getPlayer(Object o)
	{
		if(list == null || list.players == null) return null;
		return list.players.getObj(o);
	}
	
	// Level enum //
	
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
		
		public boolean isPublic()
		{ return this == PUBLIC; }
		
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
}