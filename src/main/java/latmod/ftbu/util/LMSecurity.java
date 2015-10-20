package latmod.ftbu.util;

import cpw.mods.fml.relauncher.*;
import ftb.lib.LMNBTUtils;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.ftbu.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;

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
	{ return (ownerID == 0) ? null : LMWorld.getWorld().getPlayer(ownerID); }
	
	public void setOwner(Object o)
	{ ownerID = (o == null) ? 0 : LMWorld.getWorld().getPlayerID(o); }
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		ownerID = 0;
		level = Level.PUBLIC;
		
		if(!tag.hasKey(s)) return;
		
		NBTTagCompound tag1 = tag.getCompoundTag(s);
		
		if(tag1.func_150299_b("Owner") == LMNBTUtils.STRING)
		{
			String o = tag1.getString("Owner");
			
			if(o != null && !o.isEmpty())
				ownerID = LMWorld.getWorld().getPlayerID(o);
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
	{ return getOwner() != null; }
	
	public boolean isOwner(Object player)
	{ return hasOwner() && getOwnerID() == LMWorld.getWorld().getPlayerID(player); }
	
	public boolean canInteract(Object player)
	{
		if(level == Level.PUBLIC || getOwner() == null) return true;
		if(player == null) return false;
		if(isOwner(player)) return true;
		if(player instanceof EntityPlayer && FTBUConfigGeneral.allowInteractSecure((EntityPlayer)player))
			return true;
		if(level == Level.PRIVATE) return false;
		LMPlayer owner = getOwner();
		if(level == Level.FRIENDS && owner.isFriend(LMWorld.getWorld().getPlayer(player)))
			return true;
		
		return false;
	}
	
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
		
		@SideOnly(Side.CLIENT)
		public String getText()
		{ return FTBU.mod.translateClient("security." + uname); }
		
		@SideOnly(Side.CLIENT)
		public String getTitle()
		{ return FTBU.mod.translateClient("security"); }
	}

	public void printOwner(ICommandSender ep)
	{ ep.addChatMessage(new ChatComponentTranslation(FTBU.mod.assets + "owner", hasOwner() ? getOwner().getName() : "null")); }
}