package latmod.ftbu.core.world;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.api.LMPlayerClientEvent;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.inv.LMInvUtils;
import latmod.ftbu.core.util.FastList;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class LMPlayerClient extends LMPlayer // LMPlayerServer
{
	public final FastList<IChatComponent> clientInfo;
	public final ClaimSettings claimSettings;
	private ResourceLocation skinLocation;
	public boolean isOnline;
	public int claimedChunks;
	public int maxClaimPower;
	
	public LMPlayerClient(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		clientInfo = new FastList<IChatComponent>();
		claimSettings = new ClaimSettings();
		skinLocation = null;
		isOnline = false;
	}
	
	public ResourceLocation getSkin()
	{
		if(skinLocation == null)
			skinLocation = LatCoreMCClient.getSkinTexture(getName());
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
	{ return isOnline() ? LatCoreMCClient.getPlayerSP(getUUID()) : null; }
	
	public void receiveInfo(NBTTagList tag)
	{
		clientInfo.clear();
		for(int i = 0; i < tag.tagCount(); i++)
			clientInfo.add(IChatComponent.Serializer.func_150699_a(tag.getStringTagAt(i)));
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
			chatLinks = tag.getBoolean("CL");
			chunkMessages = tag.getByte("CM");
			claimSettings.readFromNBT(tag.getCompoundTag("SC"));
			Mail.readFromNBT(this, tag, "Mail");
		}
	}
	
	public void onPostLoaded()
	{ new LMPlayerClientEvent.DataLoaded(this).post(); }
}