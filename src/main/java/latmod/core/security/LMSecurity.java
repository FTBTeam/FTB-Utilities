package latmod.core.security;

import java.util.UUID;

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
		String ow = tag.getString("Sec_" + s + "_Owner");
		if(ow != null && ow.length() > 0) owner = UUID.fromString(ow);
		else owner = null;
		
		level = Level.VALUES[tag.getByte("Sec_" + s + "_Level")];
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		if(owner != null) tag.setString("Sec_" + s + "_Owner", owner.toString());
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
		
		JsonPlayer player = JsonPlayer.getPlayer(owner);
		
		if(player != null)
		{
			String idS = id.toString();
			
			if(level == Level.WHITELIST)
				return player.whitelist.contains(idS);
			
			if(level == Level.BLACKLIST)
				return !player.blacklist.contains(idS);
		}
		
		return false;
	}
	
	public boolean canInteract(EntityPlayer ep)
	{ return canInteract((ep == null) ? null : ep.getUniqueID()); }
	
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