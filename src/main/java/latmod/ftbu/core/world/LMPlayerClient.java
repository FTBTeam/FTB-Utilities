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
public class LMPlayerClient extends LMPlayer
{
	public final FastList<String> clientInfo;
	public boolean isOnline;
	public int claimedChunks;
	public int maxClaimPower;
	
	public LMPlayerClient(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		clientInfo = new FastList<String>();
		isOnline = false;
	}
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient toPlayerSP()
	{ return this; }
	
	public EntityPlayerSP getPlayerSP()
	{ return LatCoreMCClient.getPlayerSP(getUUID()); }
	
	public boolean isOnline()
	{ return isOnline; }
	
	public void receiveInfo(NBTTagCompound tag)
	{
		LMNBTUtils.toStringList(clientInfo, tag.getTagList("I", LMNBTUtils.STRING));
		
		if(!isOnline() && tag.hasKey("L")) clientInfo.add("Last seen " + LatCore.getTimeAgo(tag.getLong("L")) + " ago");
		if(tag.hasKey("J")) clientInfo.add("Joined " + LatCore.getTimeAgo(tag.getLong("J")) + " ago");
		if(deaths > 0) clientInfo.add("Deaths: " + deaths);
		
		new LMPlayerClientEvent.CustomInfo(this, clientInfo).post();
	}
	
	public void readFromNet(NBTTagCompound tag)
	{
		isOnline = tag.getBoolean("On");
		claimedChunks = tag.getInteger("Claimed");
		maxClaimPower = tag.getInteger("MaxClaimed");
		
		friends.clear();
		friends.addAll(tag.getIntArray("F"));
		
		commonData = tag.getCompoundTag("CD");
		LMInvUtils.readItemsFromNBT(lastArmor, tag, "LI");
		deaths = tag.getInteger("D");
	}
	
	public void onPostLoaded()
	{ new LMPlayerClientEvent.DataLoaded(this).post(); }
}