package latmod.ftbu.core.world;

import latmod.ftbu.core.*;
import latmod.ftbu.core.event.LMPlayerEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.claims.Claims;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;

public class LMPlayerServer extends LMPlayer
{
	public static int lastPlayerID = 0;
	
	public static final int nextPlayerID()
	{ return ++lastPlayerID; }
	
	public NBTTagCompound serverData;
	public EntityPos lastPos, lastDeath;
	private long lastSeen;
	private long firstJoined;
	public final Claims claims;
	private String playerName;
	
	public LMPlayerServer(LMWorldServer w, int i, GameProfile gp)
	{
		super(w, i, gp);
		serverData = new NBTTagCompound();
		claims = new Claims(this);
		playerName = gp.getName();
	}
	
	public String getName()
	{ return playerName; }
	
	public void setName(String s)
	{ playerName = s; }
	
	public LMPlayerServer toPlayerMP()
	{ return this; }
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient toPlayerSP()
	{ return null; }
	
	public EntityPlayerMP getPlayerMP()
	{ return LatCoreMC.getPlayerMP(getUUID()); }
	
	public void sendUpdate(String action, boolean updateClient)
	{
		if(action == null || action.isEmpty()) action = ACTION_GENERAL;
		new LMPlayerEvent.DataChanged(this, Side.SERVER, action).post();
		if(updateClient) MessageLM.NET.sendToAll(new MessageLMPlayerUpdate(this, action));
	}
	
	public void updateLastSeen()
	{
		lastSeen = LatCore.millis();
		if(firstJoined <= 0L)
			firstJoined = lastSeen;
	}
	
	public boolean isOP()
	{ return LatCoreMC.getServer().getConfigurationManager().func_152596_g(gameProfile); }
	
	public EntityPos getLastPos()
	{
		if(isOnline())
		{
			EntityPlayerMP ep = getPlayerMP();
			if(ep != null) return new EntityPos(ep);
		}
		
		return lastPos;
	}
	
	// Reading / Writing //
	
	public MessageLMPlayerInfo getInfo()
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		long ms = LatCore.millis();
		
		FastList<String> info = new FastList<String>();
		new LMPlayerEvent.CustomInfo(this, Side.SERVER, info).post();
		tag.setTag("I", NBTHelper.fromStringList(info));
		
		if(lastSeen > 0L) tag.setLong("L", ms - lastSeen);
		if(firstJoined > 0L) tag.setLong("J", ms - firstJoined);
		if(deaths > 0) tag.setShort("D", (short)deaths);
		
		//LatCoreMC.printChat(ep, "Sending info: " + tag);
		return new MessageLMPlayerInfo(playerID, tag);
	}
	
	public void readFromServer(NBTTagCompound tag)
	{
		friends.clear();
		friends.addAll(tag.getIntArray("Friends"));
		
		commonData = tag.getCompoundTag("CustomData");
		
		InvUtils.readItemsFromNBT(lastArmor, tag, "LastItems");
		
		deaths = tag.getInteger("Deaths");
		
		serverData = tag.getCompoundTag("ServerData");
		
		if(tag.hasKey("LastPos"))
		{
			if(lastPos == null) lastPos = new EntityPos();
			lastPos.readFromNBT(tag.getCompoundTag("LastPos"));
		}
		else lastPos = null;
		
		if(tag.hasKey("LastDeath"))
		{
			if(lastDeath == null) lastDeath = new EntityPos();
			lastDeath.readFromNBT(tag.getCompoundTag("LastDeath"));
		}
		else lastDeath = null;
		
		lastSeen = tag.getLong("LastSeen");
		firstJoined = tag.getLong("Joined");
		
		claims.readFromNBT(tag);
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		if(!friends.isEmpty())
			tag.setIntArray("Friends", friends.toArray());
		
		if(!commonData.hasNoTags()) tag.setTag("CustomData", commonData);
		
		InvUtils.writeItemsToNBT(lastArmor, tag, "LastItems");
		
		tag.setInteger("Deaths", deaths);
		
		if(!serverData.hasNoTags()) tag.setTag("ServerData", serverData);
		
		if(lastPos != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			lastPos.writeToNBT(tag1);
			tag.setTag("LastPos", tag1);
		}
		
		if(lastDeath != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			lastDeath.writeToNBT(tag1);
			tag.setTag("LastDeath", tag1);
		}
		
		tag.setLong("LastSeen", lastSeen);
		tag.setLong("Joined", firstJoined);
		
		claims.writeToNBT(tag);
	}
	
	public void writeToNet(NBTTagCompound tag) // MID, LID, ID, N
	{
		if(isOnline) tag.setBoolean("On", isOnline);
		
		if(!friends.isEmpty())
			tag.setIntArray("F", friends.toArray());
		
		if(!commonData.hasNoTags()) tag.setTag("CD", commonData);
		InvUtils.writeItemsToNBT(lastArmor, tag, "LI");
		if(deaths > 0) tag.setInteger("D", deaths);
	}
}