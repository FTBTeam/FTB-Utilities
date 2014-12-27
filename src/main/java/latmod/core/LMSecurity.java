package latmod.core;

import java.util.UUID;

import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LMSecurity
{
	public UUID owner;
	public Level level;
	private TwoObjects<String, Boolean> group = null;
	
	public LMSecurity(UUID id)
	{
		owner = id;
		if(owner == null)
			owner = UUID.randomUUID();
		level = Level.PUBLIC;
		group = null;
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
		
		LMPlayer o = LMPlayer.getPlayer(owner);
		LMPlayer p = LMPlayer.getPlayer(id);
		
		if(o != null && p != null)
		{
			if(level == Level.FRIENDS) return o.isFriend(p);
			if(level == Level.CUSTOM && group != null)
			{
				boolean has = o.getGroupsFor(p.uuid).contains(group.object1);
				return (has && group.object2) || (!has && !group.object2);
			}
		}
		
		return false;
	}
	
	public boolean canInteract(EntityPlayer ep)
	{ return canInteract((ep == null) ? null : ep.getUniqueID()); }
	
	public void setGroup(String s, boolean b)
	{
		if(s == null || s.isEmpty()) group = null;
		else group = new TwoObjects<String, Boolean>(s, b);
	}
	
	// Level enum //
	
	public static enum Level
	{
		PUBLIC("public"),
		PRIVATE("private"),
		FRIENDS("friends"),
		CUSTOM("custom");
		
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
		{ return this == FRIENDS || this == CUSTOM; }
		
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