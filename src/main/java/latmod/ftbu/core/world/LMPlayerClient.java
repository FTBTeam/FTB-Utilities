package latmod.ftbu.core.world;

import latmod.ftbu.core.LMNBTUtils;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.event.LMPlayerClientEvent;
import latmod.ftbu.core.inv.LMInvUtils;
import latmod.ftbu.core.util.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LMPlayerClient extends LMPlayer // LMPlayerServer
{
	public final FastList<String> clientInfo;
	public boolean isOnline;
	
	public int claimedChunks;
	public int maxClaimPower;
	public boolean safeChunks;
	
	public LMPlayerClient(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		clientInfo = new FastList<String>();
		isOnline = false;
	}
	
	public boolean isOnline()
	{ return isOnline; }
	
	public LMPlayerServer toPlayerMP()
	{ return null; }
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient toPlayerSP()
	{ return this; }
	
	public EntityPlayerSP getPlayer()
	{ return isOnline() ? LatCoreMCClient.getPlayerSP(getUUID()) : null; }
	
	public void receiveInfo(NBTTagCompound tag)
	{
		LMNBTUtils.toStringList(clientInfo, tag.getTagList("I", LMNBTUtils.STRING));
		
		lastSeen = tag.getLong("L");
		if(!isOnline() && lastSeen > 0L) clientInfo.add("Last seen " + LMStringUtils.getTimeAgo(lastSeen) + " ago");
		
		firstJoined = tag.getLong("J");
		if(firstJoined > 0L) clientInfo.add("Joined " + LMStringUtils.getTimeAgo(firstJoined) + " ago");
		
		if(deaths > 0) clientInfo.add("Deaths: " + deaths);
		
		new LMPlayerClientEvent.CustomInfo(this, clientInfo).post();
	}
	
	public void readFromNet(NBTTagCompound tag, boolean self)
	{
		isOnline = tag.getBoolean("ON");
		
		friends.clear();
		friends.addAll(tag.getIntArray("F"));
		
		commonPublicData = tag.getCompoundTag("CD");
		LMInvUtils.readItemsFromNBT(lastArmor, tag, "LI");
		deaths = tag.getInteger("D");
		
		if(self)
		{
			commonPrivateData = tag.getCompoundTag("CPD");
			
			claimedChunks = tag.getInteger("CC");
			maxClaimPower = tag.getInteger("MCC");
			safeChunks = tag.getBoolean("SC");
			chatLinks = tag.getBoolean("CL");
			chunkMessages = tag.getByte("CM");
		}
	}
	
	public void onPostLoaded()
	{ new LMPlayerClientEvent.DataLoaded(this).post(); }
}