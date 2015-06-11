package latmod.ftbu.core;

import java.util.UUID;

import latmod.ftbu.FTBU;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LMSecurity
{
	private int ownerID;
	public Level level;
	
	public LMSecurity(Object o)
	{
		setOwner(o);
		level = Level.PUBLIC;
	}
	
	public int getOwnerID()
	{ return ownerID; }
	
	public LMPlayer getOwner()
	{ return LMPlayer.getPlayer(ownerID); }
	
	public void setOwner(Object o)
	{ ownerID = LMPlayer.getPlayerID(o); }
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		ownerID = 0;
		level = Level.PUBLIC;
		
		if(!tag.hasKey(s)) return;
		
		NBTTagCompound tag1 = tag.getCompoundTag(s);
		
		if(tag1.func_150299_b("Owner") == NBTHelper.STRING)
		{
			String o = tag1.getString("Owner");
			
			if(o != null && !o.isEmpty())
				ownerID = LMPlayer.getPlayerID(o);
		}
		else
		{
			ownerID = tag1.getInteger("Owner");
		}
		
		level = Level.VALUES[tag1.getByte("Level")];
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		if(ownerID > 0)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setInteger("Owner", ownerID);
			tag1.setByte("Level", (byte)level.ID);
			tag.setTag(s, tag1);
		}
	}
	
	public boolean hasOwner()
	{ return ownerID > 0; }
	
	public boolean isOwner(Object o)
	{ return hasOwner() && getOwnerID() == LMPlayer.getPlayerID(o); }
	
	public boolean canInteract(UUID id)
	{
		if(level == Level.PUBLIC || ownerID == 0) return true;
		if(id == null) return false;
		if(isOwner(id)) return true;
		if(level == Level.PRIVATE) return false;
		
		LMPlayer owner = getOwner();
		if(level == Level.FRIENDS && owner != null && owner.isFriend(LMPlayer.getPlayer(id)))
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
		{ return FTBU.mod.translate("security." + uname); }
		
		public String getTitle()
		{ return FTBU.mod.translate("security"); }
	}
}