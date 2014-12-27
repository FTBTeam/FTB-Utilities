package latmod.core;

import java.util.UUID;

import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LMSecurity
{
	public LMPlayer owner;
	public Level level;
	private TwoObjects<String, Boolean> group;
	
	public LMSecurity(Object o)
	{
		setOwner(o);
		level = Level.PUBLIC;
		group = null;
	}
	
	public void setOwner(Object o)
	{ owner = LMPlayer.getPlayer(o); }
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		if(tag.hasKey("Sec_" + s + "_Owner") && tag.hasKey("Sec_" + s + "_Level"))
		{
			owner = LMPlayer.getPlayer(tag.getString("Sec_" + s + "_Owner"));
			level = Level.VALUES[tag.getByte("Sec_" + s + "_Level")];
			if(level == Level.CUSTOM) level = Level.PRIVATE;
			group = null;
		}
		else
		{
			NBTTagCompound tag1 = tag.getCompoundTag(s);
			
			String o = tag1.getString("Owner");
			
			if(o == null || o.isEmpty())
			{
				owner = null;
				level = Level.PUBLIC;
				group = null;
			}
			else
			{
				owner = LMPlayer.getPlayer(o);
				level = Level.VALUES[tag1.getByte("Level")];
				
				if(tag1.hasKey("Group"))
				{
					String g = tag1.getString("Group");
					boolean w = tag1.getBoolean("Whitelist");
					group = new TwoObjects<String, Boolean>(g, w);
				}
				else group = null;
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		NBTTagCompound tag1 = new NBTTagCompound();
		
		if(owner != null)
		{
			tag1.setString("Owner", owner.username);
			tag1.setByte("Level", (byte)level.ID);
			
			if(group != null && group.object1 != null && group.object2 != null)
			{
				tag1.setString("Group", group.object1);
				tag1.setBoolean("Whitelist", group.object2);
			}
			
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
		
		LMPlayer p = LMPlayer.getPlayer(id);
		
		if(p != null)
		{
			if(level == Level.FRIENDS) return owner.isFriend(p);
			if(level == Level.CUSTOM && group != null)
			{
				boolean has = owner.getGroupsFor(p.uuid).contains(group.object1);
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