package latmod.core.util;

import java.util.UUID;

import latmod.core.LatCore;
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
		if(!tag.hasKey(s))
		{
			owner = null;
			level = Level.PUBLIC;
			group = null;
			return;
		}
		
		NBTTagCompound tag1 = tag.getCompoundTag(s);
		
		if(tag1.func_150299_b("Owner") == NBTHelper.STRING)
		{
			String o = tag1.getString("Owner");
			
			if(o == null || o.isEmpty())
			{
				owner = null;
				level = Level.PUBLIC;
				group = null;
			}
			else owner = LMPlayer.getPlayer(o);
		}
		else
		{
			int o = tag1.getInteger("Owner");
			
			if(o == 0)
			{
				owner = null;
				level = Level.PUBLIC;
				group = null;
			}
			else owner = LMPlayer.getPlayer(o);
		}
		
		level = Level.VALUES[tag1.getByte("Level")];
		
		if(tag1.hasKey("Group"))
		{
			String g = tag1.getString("Group");
			boolean w = tag1.getBoolean("Whitelist");
			group = new TwoObjects<String, Boolean>(g, w);
		}
		else group = null;
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		if(owner != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			tag1.setInteger("Owner", owner.playerID);
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
			if(level == Level.GROUP && group != null)
			{
				LMPlayer.Group g = owner.groups.get(group.object1);
				boolean has = (g != null && g.members.contains(id));
				return has == group.object2.booleanValue();
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
		GROUP("group");
		
		public static final Level[] VALUES = values();
		public static final Level[] VALUES_2 = new Level[] { PUBLIC, PRIVATE };
		public static final Level[] VALUES_3 = new Level[] { PUBLIC, PRIVATE, FRIENDS };
		
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
		{ return this == FRIENDS || this == GROUP; }
		
		public Level next(Level[] l)
		{ return l[(ID + 1) % l.length]; }
		
		public Level prev(Level[] l)
		{
			int id = ID - 1;
			if(id < 0) id = l.length - 1;
			return l[id];
		}
		
		public String getText()
		{ return LatCore.mod.translate("security." + uname); }
		
		public String getTitle()
		{ return LatCore.mod.translate("security"); }
	}
}