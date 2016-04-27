package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.client.FTBLibClient;
import ftb.utils.api.EventLMPlayerClient;
import latmod.lib.Bits;
import latmod.lib.IntMap;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class LMPlayerClient extends LMPlayer // LMPlayerServer // LMPlayerClientSelf
{
	public final List<String> clientInfo;
	public boolean isOnline;
	
	public LMPlayerClient(GameProfile gp)
	{
		super(gp);
		clientInfo = new ArrayList<>();
		isOnline = false;
	}
	
	public ResourceLocation getSkin()
	{ return FTBLibClient.getSkinTexture(getProfile().getName()); }
	
	@Override
	public LMWorldClient getWorld()
	{ return LMWorldClient.inst; }
	
	@Override
	public Side getSide()
	{ return Side.CLIENT; }
	
	@Override
	public boolean isOnline()
	{ return isOnline; }
	
	@Override
	public LMPlayerServer toPlayerMP()
	{ return null; }
	
	@Override
	public LMPlayerClient toPlayerSP()
	{ return this; }
	
	public LMPlayerClientSelf toPlayerSPSelf()
	{ return null; }
	
	@Override
	public EntityPlayerSP getPlayer()
	{ return isOnline() ? FTBLibClient.getPlayerSP(getProfile().getId()) : null; }
	
	@SideOnly(Side.CLIENT)
	public void receiveInfo(List<IChatComponent> info)
	{
		clientInfo.clear();
		
		for(IChatComponent c : info)
		{
			clientInfo.add(c.getFormattedText());
		}
		
		new EventLMPlayerClient.CustomInfo(this, clientInfo).post();
	}
	
	public void readFromNet(NBTTagCompound tag, boolean self)
	{
		IntMap map = new IntMap();
		map.list.addAll(tag.getIntArray("S"));
		
		isOnline = map.get(0) != 0;
		renderBadge = map.get(1) != 0;
		
		friendsList.clear();
		friendsList.addAll(Bits.toUUIDList(tag.getByteArray("F")));
		
		List<UUID> otherFriends = Bits.toUUIDList(tag.getByteArray("OF"));
		
		for(LMPlayerClient p : LMWorldClient.inst.playerMap.values())
		{
			if(!p.equalsPlayer(this))
			{
				p.friendsList.clear();
				if(otherFriends.contains(p.getProfile().getId()))
				{
					p.friendsList.add(getProfile().getId());
					otherFriends.remove(p.getProfile().getId());
				}
			}
		}
		
		commonPublicData = tag.hasKey("CPUD") ? tag.getCompoundTag("CPUD") : null;
	}
}