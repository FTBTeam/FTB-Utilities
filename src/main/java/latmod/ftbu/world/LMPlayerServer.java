package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import latmod.core.util.*;
import latmod.ftbu.api.EventLMPlayerServer;
import latmod.ftbu.inv.LMInvUtils;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.net.*;
import latmod.ftbu.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
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
	
	public void sendUpdate(boolean updateClient)
	{
		new EventLMPlayerServer.DataChanged(this).post();
		if(updateClient)
		{
			LMNetHelper.sendTo(getPlayer(), new MessageLMPlayerUpdate(this, true));
			for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers(getPlayer()))
				LMNetHelper.sendTo(ep, new MessageLMPlayerUpdate(this, false));
		}
	}
	
	public void updateLastSeen()
	{
		lastSeen = LMUtils.millis();
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
			EntityPlayerMP ep = getPlayer();
			if(ep != null) return new EntityPos(ep);
		}
		
		return lastPos;
	}
	
	// Reading / Writing //
	
	public NBTTagList getInfo()
	{
		long ms = LMUtils.millis();
		
		FastList<IChatComponent> info = new FastList<IChatComponent>();
		new EventLMPlayerServer.CustomInfo(this, info).post();
		
		if(lastSeen > 0L && !isOnline()) info.add(new ChatComponentTranslation("ftbu:label.last_seen", LMStringUtils.getTimeString(ms - lastSeen)));
		if(firstJoined > 0L) info.add(new ChatComponentTranslation("ftbu:label.joined", LMStringUtils.getTimeString(ms - firstJoined)));
		if(deaths > 0) info.add(new ChatComponentTranslation("ftbu:label.deaths", String.valueOf(deaths)));
		
		NBTTagList list = new NBTTagList();
		for(IChatComponent c : info)
			list.appendTag(new NBTTagString(IChatComponent.Serializer.func_150696_a(c)));
		return list;
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
		
		Mail.readFromNBT(this, tag, "Mail");
		settings.readFromServer(tag.getCompoundTag("Settings"));
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
		
		Mail.writeToNBT(this, tag, "Mail");
		
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToServer(settingsTag);
		tag.setTag("Settings", settingsTag);
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		if(isOnline()) tag.setBoolean("ON", true);
		
		if(!friends.isEmpty())
			tag.setIntArray("F", friends.toArray());
		
		if(!commonPublicData.hasNoTags()) tag.setTag("CD", commonPublicData);
		LMInvUtils.writeItemsToNBT(lastArmor, tag, "LI");
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
			maxClaimPower = FTBUConfig.general.maxClaims;
			EventLMPlayerServer.GetMaxClaimPower e = new EventLMPlayerServer.GetMaxClaimPower(this, maxClaimPower);
			e.post();
			maxClaimPower = e.result;
		}
		
		return maxClaimPower;
	}
}