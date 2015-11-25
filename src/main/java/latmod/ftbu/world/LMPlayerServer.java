package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.item.StringIDInvLoader;
import latmod.ftbu.api.EventLMPlayerServer;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigClaims;
import latmod.ftbu.net.MessageLMPlayerUpdate;
import latmod.ftbu.notification.*;
import latmod.ftbu.util.LatCoreMC;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;

public class LMPlayerServer extends LMPlayer // LMPlayerClient
{
	public static int lastPlayerID = 0;
	
	public static final int nextPlayerID()
	{ return ++lastPlayerID; }
	
	public long adminToken = 0L;
	public NBTTagCompound serverData;
	public EntityPos lastPos, lastDeath;
	public final Claims claims;
	public final LMPlayerStats stats;
	private String playerName;
	private EntityPlayerMP entityPlayer = null;
	public int lastChunkType = -99;
	public final Warps homes;
	
	public LMPlayerServer(LMWorldServer w, int i, GameProfile gp)
	{
		super(w, i, gp);
		serverData = new NBTTagCompound();
		claims = new Claims(this);
		stats = new LMPlayerStats(this);
		playerName = gp.getName();
		homes = new Warps();
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
		if(isOnline()) new MessageLMPlayerUpdate(this, true).sendTo(getPlayer());
		for(EntityPlayerMP ep : FTBLib.getAllOnlinePlayers(getPlayer()))
			new MessageLMPlayerUpdate(this, false).sendTo(ep);
	}
	
	public boolean isOP()
	{ return FTBLib.isOP(gameProfile); }
	
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
		stats.getInfo(info, ms);
	}
	
	public void refreshStats()
	{
		if(isOnline())
		{
			stats.refreshStats();
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
		
		stats.readFromNBT(tag.getCompoundTag("Stats"));
		
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
		
		claims.readFromNBT(tag);
		
		settings.readFromServer(tag.getCompoundTag("Settings"));
		
		homes.readFromNBT(tag, "Homes");
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		refreshStats();
		
		if(!friends.isEmpty())
			tag.setIntArray("Friends", friends.toArray());
		
		if(!commonPublicData.hasNoTags()) tag.setTag("CustomData", commonPublicData);
		if(!commonPrivateData.hasNoTags()) tag.setTag("CustomPrivateData", commonPrivateData);
		
		StringIDInvLoader.writeItemsToNBT(lastArmor, tag, "LastItems");
		
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
		
		NBTTagCompound statsTag = new NBTTagCompound();
		stats.writeToNBT(statsTag);
		tag.setTag("Stats", statsTag);
		
		claims.writeToNBT(tag);
		
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToServer(settingsTag);
		tag.setTag("Settings", settingsTag);
		
		homes.writeToNBT(tag, "Homes");
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		refreshStats();
		
		if(isOnline()) tag.setBoolean("ON", true);
		
		if(!friends.isEmpty())
			tag.setIntArray("F", friends.toArray());
		
		if(!commonPublicData.hasNoTags()) tag.setTag("CD", commonPublicData);
		
		if(self)
		{
			if(!commonPrivateData.hasNoTags()) tag.setTag("CPD", commonPrivateData);
			if(claims.getClaimedChunks() > 0) tag.setInteger("CC", claims.getClaimedChunks());
			tag.setInteger("MCC", getMaxClaimPower());
		}
		
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToNet(settingsTag, self);
		tag.setTag("CFG", settingsTag);
	}
	
	public void onPostLoaded()
	{ new EventLMPlayerServer.DataLoaded(this).post(); }
	
	public int getMaxClaimPower()
	{
		return isOP() ? FTBUConfigClaims.maxClaimsAdmin.get() : FTBUConfigClaims.maxClaimsPlayer.get();
		
		/*
		if(maxClaimPower == -1)
		{
			maxClaimPower = isOP() ? FTBUConfigClaims.maxClaimsAdmin.get() : FTBUConfigClaims.maxClaimsPlayer.get();
			EventLMPlayerServer.GetMaxClaimPower e = new EventLMPlayerServer.GetMaxClaimPower(this, maxClaimPower);
			e.post();
			maxClaimPower = Math.max(0, e.result);
		}
		
		return maxClaimPower;
		*/
	}
	
	public void checkNewFriends()
	{
		if(isOnline())
		{
			FastList<String> requests = new FastList<String>();
			
			for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
			{
				LMPlayer p1 = LMWorldServer.inst.players.get(i);
				if(p1.isFriendRaw(this) && !isFriendRaw(p1))
					requests.add(p1.getName());
			}
			
			if(requests.size() > 0)
			{
				IChatComponent cc = new ChatComponentTranslation(FTBU.mod.assets + "label.new_friends");
				cc.getChatStyle().setColor(EnumChatFormatting.GREEN);
				Notification n = new Notification("new_friend_requests", cc, 6000);
				n.setDesc(new ChatComponentTranslation(FTBU.mod.assets + "label.new_friends_click"));
				
				MouseAction mouse = new MouseAction(ClickAction.FRIEND_ADD_ALL, null);
				requests.sort(null);
				mouse.hover = new IChatComponent[requests.size()];
				for(int i = 0; i < mouse.hover.length; i++)
					mouse.hover[i] = new ChatComponentText(requests.get(i));
				n.setMouseAction(mouse);
				
				LatCoreMC.notifyPlayer(getPlayer(), n);
			}
		}
	}
}