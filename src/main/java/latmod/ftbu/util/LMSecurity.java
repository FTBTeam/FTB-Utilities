package latmod.ftbu.util;

import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;

public class LMSecurity
{
	private int ownerID;
	public LMSecurityLevel level;
	
	public LMSecurity(Object o)
	{
		setOwner(o);
		level = LMSecurityLevel.PUBLIC;
	}
	
	public int getOwnerID()
	{ return ownerID; }
	
	public LMPlayer getOwner()
	{ return (ownerID == 0) ? null : LMWorld.getWorld().getPlayer(ownerID); }
	
	public void setOwner(Object o)
	{ ownerID = (o == null) ? 0 : LMWorld.getWorld().getPlayerID(o); }
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		if(tag.hasKey(s))
		{
			NBTTagCompound tag1 = tag.getCompoundTag(s);
			ownerID = tag1.getInteger("Owner");
			level = LMSecurityLevel.VALUES_3[tag1.getByte("Level")];
		}
		else
		{
			ownerID = 0;
			level = LMSecurityLevel.PUBLIC;
		}
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		if(ownerID > 0 || level != LMSecurityLevel.PUBLIC)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setInteger("Owner", ownerID);
			tag1.setByte("Level", (byte) level.ID);
			tag.setTag(s, tag1);
		}
	}
	
	public boolean hasOwner()
	{ return getOwner() != null; }
	
	public boolean isOwner(EntityPlayer ep)
	{ return isOwner(LMWorld.getWorld().getPlayer(ep)); }
	
	public boolean isOwner(LMPlayer player)
	{ return hasOwner() && getOwnerID() == player.playerID; }
	
	public boolean canInteract(EntityPlayer ep)
	{ return canInteract(LMWorld.getWorld().getPlayer(ep)); }
	
	public boolean canInteract(LMPlayer playerLM)
	{
		if(level == LMSecurityLevel.PUBLIC || getOwner() == null) return true;
		if(playerLM == null) return false;
		if(isOwner(playerLM)) return true;
		if(playerLM != null && playerLM.isOnline() && playerLM.getRank().config.allowCreativeInteractSecure(playerLM.getPlayer()))
			return true;
		if(level == LMSecurityLevel.PRIVATE) return false;
		LMPlayer owner = getOwner();
		if(level == LMSecurityLevel.FRIENDS && owner.isFriend(playerLM)) return true;
		
		return false;
	}
	
	public void printOwner(ICommandSender ep)
	{ ep.addChatMessage(new ChatComponentTranslation(FTBU.mod.assets + "owner", hasOwner() ? getOwner().getName() : "null")); }
}