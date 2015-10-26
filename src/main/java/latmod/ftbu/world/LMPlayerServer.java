package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.item.StringIDInvLoader;
import latmod.ftbu.api.EventLMPlayerServer;
import latmod.ftbu.mod.config.FTBUConfigClaims;
import latmod.ftbu.net.MessageLMPlayerUpdate;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.*;
import net.minecraft.util.*;

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
	public int lastChunkType = -99;
	
	public long lastSeen, firstJoined, timePlayed;
	
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
	
	public boolean isOnline()
	{ return entityPlayer != null; }
	
	public LMPlayerServer toPlayerMP()
	{ return this; }
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient toPlayerSP()
	{ return null; }
	
	public EntityPlayerMP getPlayer()
	{ return entityPlayer; }
	
	public void setPlayer(EntityPlayerMP ep)
	{ entityPlayer = ep; }
	
	public void sendUpdate()
	{
		new EventLMPlayerServer.DataChanged(this).post();
		new MessageLMPlayerUpdate(this, true).sendTo(getPlayer());
		for(EntityPlayerMP ep : FTBLib.getAllOnlinePlayers(getPlayer()))
			new MessageLMPlayerUpdate(this, false).sendTo(ep);
	}
	
	public boolean isOP()
	{ return FTBLib.getServer().getConfigurationManager().func_152596_g(gameProfile); }
	
	public EntityPos getPos()
	{
		EntityPlayerMP ep = getPlayer();
		if(ep != null)
		{
			if(lastPos == null) lastPos = new EntityPos(ep);
			else lastPos.set(ep);
		}
		return lastPos;
	}
	
	// Reading / Writing //
	
	public void getInfo(FastList<IChatComponent> info)
	{
		refreshStats();
		
		long ms = LMUtils.millis();
		new EventLMPlayerServer.CustomInfo(this, info).post();
		
		if(lastSeen > 0L && !isOnline()) info.add(new ChatComponentTranslation("ftbu:label.last_seen", LMStringUtils.getTimeString(ms - lastSeen)));
		if(firstJoined > 0L) info.add(new ChatComponentTranslation("ftbu:label.joined", LMStringUtils.getTimeString(ms - firstJoined)));
		if(deaths > 0) info.add(new ChatComponentTranslation("ftbu:label.deaths", String.valueOf(deaths)));
		if(timePlayed > 0L)
			info.add(new ChatComponentTranslation("stat.playOneMinute").appendSibling(new ChatComponentText(": " + LMStringUtils.getTimeString(timePlayed))));
	}
	
	public int getStat(StatBase s)
	{ return isOnline() ? getPlayer().func_147099_x().writeStat(s) : 0; }
	
	public void refreshStats()
	{
		if(isOnline())
		{
			timePlayed = getStat(StatList.minutesPlayedStat) * 50L;
			deaths = getStat(StatList.deathsStat);
			
			lastSeen = LMUtils.millis();
			if(firstJoined <= 0L)
				firstJoined = lastSeen;
			
			if(!world.settings.isOutsideF(entityPlayer.dimension, entityPlayer.posX, entityPlayer.posZ))
				getPos();
		}
	}
	
	public void readFromServer(NBTTagCompound tag)
	{
		friends.clear();
		friends.addAll(tag.getIntArray("Friends"));
		
		commonPublicData = tag.getCompoundTag("CustomData");
		commonPrivateData = tag.getCompoundTag("CustomPrivateData");
		
		StringIDInvLoader.readItemsFromNBT(lastArmor, tag, "LastItems");
		
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
		timePlayed = tag.getLong("TimePlayed");
		
		claims.readFromNBT(tag);
		
		Mail.readFromNBT(this, tag, "Mail");
		settings.readFromServer(tag.getCompoundTag("Settings"));
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		refreshStats();
		
		if(!friends.isEmpty())
			tag.setIntArray("Friends", friends.toArray());
		
		if(!commonPublicData.hasNoTags()) tag.setTag("CustomData", commonPublicData);
		if(!commonPrivateData.hasNoTags()) tag.setTag("CustomPrivateData", commonPrivateData);
		
		StringIDInvLoader.writeItemsToNBT(lastArmor, tag, "LastItems");
		
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
		tag.setLong("TimePlayed", timePlayed);
		
		claims.writeToNBT(tag);
		
		Mail.writeToNBT(this, tag, "Mail");
		
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToServer(settingsTag);
		tag.setTag("Settings", settingsTag);
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		refreshStats();
		
		if(isOnline()) tag.setBoolean("ON", true);
		
		if(!friends.isEmpty())
			tag.setIntArray("F", friends.toArray());
		
		if(!commonPublicData.hasNoTags()) tag.setTag("CD", commonPublicData);
		
		if(deaths > 0) tag.setInteger("D", deaths);
		
		if(self)
		{
			if(!commonPrivateData.hasNoTags()) tag.setTag("CPD", commonPrivateData);
			if(claims.getClaimedChunks() > 0) tag.setInteger("CC", claims.getClaimedChunks());
			tag.setInteger("MCC", getMaxClaimPower());
			Mail.writeToNBT(this, tag, "Mail");
		}
		
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToNet(settingsTag, self);
		tag.setTag("CFG", settingsTag);
	}
	
	public void onPostLoaded()
	{ new EventLMPlayerServer.DataLoaded(this).post(); }
	
	public int updateMaxClaimPower()
	{ maxClaimPower = -1; return getMaxClaimPower(); }
	
	public int getMaxClaimPower()
	{
		if(maxClaimPower == -1)
		{
			maxClaimPower = isOP() ? FTBUConfigClaims.maxClaimsAdmin.get() : FTBUConfigClaims.maxClaimsPlayer.get();
			EventLMPlayerServer.GetMaxClaimPower e = new EventLMPlayerServer.GetMaxClaimPower(this, maxClaimPower);
			e.post();
			maxClaimPower = Math.max(0, e.result);
		}
		
		return maxClaimPower;
	}

	public double getDeathsPerHour()
	{
		if(deaths == 0 || timePlayed == 0L) return 0D;
		return (double)deaths / (timePlayed / 3600000D);
	}
}