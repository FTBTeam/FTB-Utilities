package latmod.ftbu.core.world;

import latmod.ftbu.core.*;
import latmod.ftbu.core.event.LMPlayerServerEvent;
import latmod.ftbu.core.inv.LMInvUtils;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.mod.player.Claims;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

public class LMPlayerServer extends LMPlayer // LMPlayerClient
{
	public static int lastPlayerID = 0;
	
	public static final int nextPlayerID()
	{ return ++lastPlayerID; }
	
	public NBTTagCompound serverData;
	public EntityPos lastPos, lastDeath;
	public final Claims claims;
	private String playerName;
	private EntityPlayerMP entityPlayer = null;
	private int maxClaimPower = -1;
	
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
	
	public void setPlayer(EntityPlayerMP ep)
	{ entityPlayer = ep; }
	
	public EntityPlayerMP getPlayerMP()
	//{ return LatCoreMC.getPlayerMP(getUUID()); }
	{ return entityPlayer; }
	
	public boolean isOnline()
	{ return entityPlayer != null; }
	
	public void sendUpdate(String action, boolean updateClient)
	{
		if(action == null || action.isEmpty()) action = ACTION_GENERAL;
		new LMPlayerServerEvent.DataChanged(this, action).post();
		if(updateClient)
		{
			for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers())
				LMNetHelper.sendTo(ep, new MessageLMPlayerUpdate(this, action, ep.getUniqueID().equals(getUUID())));
		}
	}
	
	public void updateLastSeen()
	{
		lastSeen = LatCore.millis();
		if(firstJoined <= 0L)
			firstJoined = lastSeen;
		if(entityPlayer != null)
		{
			if(lastPos == null)
				lastPos = new EntityPos(entityPlayer);
			else lastPos.set(entityPlayer);
		}
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
	
	public void getInfo(NBTTagCompound tag)
	{
		long ms = LatCore.millis();
		
		FastList<String> info = new FastList<String>();
		new LMPlayerServerEvent.CustomInfo(this, info).post();
		tag.setTag("I", LMNBTUtils.fromStringList(info));
		
		if(lastSeen > 0L) tag.setLong("L", ms - lastSeen);
		if(firstJoined > 0L) tag.setLong("J", ms - firstJoined);
		if(deaths > 0) tag.setShort("D", (short)deaths);
	}
	
	public void readFromServer(NBTTagCompound tag)
	{
		friends.clear();
		friends.addAll(tag.getIntArray("Friends"));
		
		commonPublicData = tag.getCompoundTag("CustomData");
		commonPrivateData = tag.getCompoundTag("CustomPrivateData");
		
		LMInvUtils.readItemsFromNBT(lastArmor, tag, "LastItems");
		
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
		
		chatLinks = tag.hasKey("ChatLinks") ? tag.getBoolean("ChatLinks") : true;
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		if(!friends.isEmpty())
			tag.setIntArray("Friends", friends.toArray());
		
		if(!commonPublicData.hasNoTags()) tag.setTag("CustomData", commonPublicData);
		if(!commonPrivateData.hasNoTags()) tag.setTag("CustomPrivateData", commonPrivateData);
		
		LMInvUtils.writeItemsToNBT(lastArmor, tag, "LastItems");
		
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
		
		tag.setBoolean("ChatLinks", chatLinks);
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		if(isOnline()) tag.setBoolean("On", true);
		
		if(!friends.isEmpty())
			tag.setIntArray("F", friends.toArray());
		
		if(!commonPublicData.hasNoTags()) tag.setTag("CD", commonPublicData);
		LMInvUtils.writeItemsToNBT(lastArmor, tag, "LI");
		if(deaths > 0) tag.setInteger("D", deaths);
		
		if(self)
		{
			if(!commonPrivateData.hasNoTags()) tag.setTag("CPD", commonPrivateData);
			if(claims.getClaimedChunks() > 0) tag.setInteger("Claimed", claims.getClaimedChunks());
			tag.setInteger("MaxClaimed", getMaxClaimPower());
			if(claims.isSafe()) tag.setBoolean("SafeChunks", true);
			if(chatLinks) tag.setBoolean("ChatLinks", chatLinks);
		}
	}
	
	public void onPostLoaded()
	{ new LMPlayerServerEvent.DataLoaded(this).post(); }
	
	public int updateMaxClaimPower()
	{ maxClaimPower = -1; return getMaxClaimPower(); }
	
	public int getMaxClaimPower()
	{
		if(maxClaimPower == -1)
		{
			maxClaimPower = FTBUConfig.general.maxClaims;
			LMPlayerServerEvent.GetMaxClaimPower e = new LMPlayerServerEvent.GetMaxClaimPower(this, maxClaimPower);
			e.post();
			maxClaimPower = e.result;
		}
		
		return maxClaimPower;
	}
}