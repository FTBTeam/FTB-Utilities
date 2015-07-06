package latmod.ftbu.core.world;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.event.LMPlayerEvent;
import latmod.ftbu.core.util.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LMPlayerClient extends LMPlayer
{
	public final FastList<String> clientInfo;
	
	public LMPlayerClient(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		clientInfo = new FastList<String>();
	}
	
	public LMPlayerServer toPlayerMP()
	{ return null; }
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient toPlayerSP()
	{ return this; }
	
	public EntityPlayerSP getPlayerSP()
	{ return LatCoreMCClient.getPlayerSP(getUUID()); }
	
	public void receiveInfo(NBTTagCompound tag)
	{
		NBTHelper.toStringList(clientInfo, tag.getTagList("I", NBTHelper.STRING));
		
		if(!isOnline() && tag.hasKey("L")) clientInfo.add("Last seen " + LatCore.getTimeAgo(tag.getLong("L")) + " ago");
		if(tag.hasKey("J")) clientInfo.add("Joined " + LatCore.getTimeAgo(tag.getLong("J")) + " ago");
		if(deaths > 0) clientInfo.add("Deaths: " + deaths);
		
		new LMPlayerEvent.CustomInfo(this, Side.CLIENT, clientInfo).post();
	}
	
	public void readFromNet(NBTTagCompound tag)
	{
		isOnline = tag.getBoolean("On");
		
		friends.clear();
		friends.addAll(tag.getIntArray("F"));
		
		commonData = tag.getCompoundTag("CD");
		InvUtils.readItemsFromNBT(lastArmor, tag, "LI");
		deaths = tag.getInteger("D");
	}
}