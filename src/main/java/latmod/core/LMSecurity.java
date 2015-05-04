package latmod.core;

import java.util.UUID;

import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LMSecurity
{
	public LMPlayer owner;
	public Level level;
	
	public LMSecurity(Object o)
	{
		setOwner(o);
		level = Level.PUBLIC;
	}
	
	public void setOwner(Object o)
	{ owner = LMPlayer.getPlayer(o); }
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		owner = null;
		level = Level.PUBLIC;
		
		if(!tag.hasKey(s)) return;
		
		NBTTagCompound tag1 = tag.getCompoundTag(s);
		
		if(tag1.func_150299_b("Owner") == NBTHelper.STRING)
		{
			String o = tag1.getString("Owner");
			
			if(o != null && !o.isEmpty())
				owner = LMPlayer.getPlayer(o);
		}
		else
		{
			int o = tag1.getInteger("Owner");
			if(o > 0) owner = LMPlayer.getPlayer(o);
		}
		
		level = Level.VALUES[tag1.getByte("Level")];
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		if(owner != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setInteger("Owner", owner.playerID);
			tag1.setByte("Level", (byte)level.ID);
			tag.setTag(s, tag1);
		}
	}
	
	public boolean isOwner(Object o)
	{ return owner != null && owner.equals(o); }
	
	public boolean canInteract(UUID id)
	{
		if(level == Level.PUBLIC || owner == null) return true;
		if(id == null) return false;
		if(isOwner(id)) return true;
		if(level == Level.PRIVATE) return false;
		
		if(level == Level.FRIENDS && owner.isFriend(LMPlayer.getPlayer(id)))
			return true;
		
		return false;
	}
	
	public boolean canInteract(EntityPlayer ep)
	{ return canInteract((ep == null) ? null : ep.getUniqueID()); }
	
	// Level enum //
	
	public static enum Level
	{
		PUBLIC("public"),
		PRIVATE("private"),
		FRIENDS("friends");
		
		public static final Level[] VALUES = values();
		public static final Level[] VALUES_2 = new Level[] { PUBLIC, PRIVATE };
		
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
		{ return this == FRIENDS; }
		
		public Level next(Level[] l)
		{ return l[(ID + 1) % l.length]; }
		
		public Level prev(Level[] l)
		{
			int id = ID - 1;
			if(id < 0) id = l.length - 1;
			return l[id];
		}
		
		public String getText()
		{ return LC.mod.translate("security." + uname); }
		
		public String getTitle()
		{ return LC.mod.translate("security"); }
	}
}