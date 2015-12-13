package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.badges.Badge;
import latmod.lib.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;

public class LMPlayerClient extends LMPlayer // LMPlayerServer
{
	public final FastList<IChatComponent> clientInfo;
	private ResourceLocation skinLocation;
	public boolean isOnline;
	public int claimedChunks;
	public int maxClaimPower;
	public Badge cachedBadge;
	
	public LMPlayerClient(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		clientInfo = new FastList<IChatComponent>();
		skinLocation = null;
		isOnline = false;
		cachedBadge = null;
	}
	
	public ResourceLocation getSkin()
	{
		if(skinLocation == null)
			skinLocation = FTBLibClient.getSkinTexture(getName());
		return skinLocation;
	}
	
	public boolean isOnline()
	{ return isOnline; }
	
	public LMPlayerServer toPlayerMP()
	{ return null; }
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient toPlayerSP()
	{ return this; }
	
	public EntityPlayerSP getPlayer()
	{ return isOnline() ? FTBLibClient.getPlayerSP(getUUID()) : null; }
	
	public void receiveInfo(FastList<IChatComponent> info)
	{
		clientInfo.clear();
		clientInfo.addAll(info);
		new EventLMPlayerClient.CustomInfo(this, clientInfo).post();
	}
	
	public void readFromNet(NBTTagCompound tag, boolean self)
	{
		isOnline = tag.getBoolean("ON");
		
		friends.clear();
		friends.addAll(tag.getIntArray("F"));
		
		IntList otherFriends = IntList.asList(tag.getIntArray("OF"));
		
		for(int i = 0; i < LMWorldClient.inst.players.size(); i++)
		{
			LMPlayer p = LMWorldClient.inst.players.get(i);
			if(p != this)
			{
				p.friends.clear();
				if(otherFriends.contains(p.playerID))
				{
					p.friends.add(playerID);
					otherFriends.removeValue(p.playerID);
				}
			}
		}
		
		commonPublicData = tag.hasKey("CD") ? tag.getCompoundTag("CD") : null;
		
		if(self)
		{
			commonPrivateData = tag.hasKey("CPD") ? tag.getCompoundTag("CPD") : null;
			claimedChunks = tag.getInteger("CC");
			maxClaimPower = tag.getInteger("MCC");
		}
		
		settings.readFromNet(tag.getCompoundTag("CFG"), self);
	}
	
	public void onReloaded()
	{
		cachedBadge = null;
	}
}