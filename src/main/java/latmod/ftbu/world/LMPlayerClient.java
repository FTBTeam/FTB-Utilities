package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.*;
import ftb.lib.LMNBTUtils;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.badges.Badge;
import latmod.ftbu.world.ranks.*;
import latmod.lib.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class LMPlayerClient extends LMPlayer // LMPlayerServer // LMPlayerClientSelf
{
	public final LMWorldClient world;
	public final FastList<IChatComponent> clientInfo;
	public boolean isOnline;
	public Badge cachedBadge;

	public LMPlayerClient(LMWorldClient w, int i, GameProfile gp)
	{
		super(i, gp);
		world = w;
		clientInfo = new FastList<>();
		isOnline = false;
		cachedBadge = null;
	}
	
	public ResourceLocation getSkin()
	{ return FTBLibClient.getSkinTexture(getName()); }
	
	public LMWorld getWorld()
	{ return world; }
	
	public Side getSide()
	{ return Side.CLIENT; }
	
	public boolean isOnline()
	{ return isOnline; }
	
	public LMPlayerServer toPlayerMP()
	{ return null; }
	
	public LMPlayerClient toPlayerSP()
	{ return this; }
	
	public LMPlayerClientSelf toPlayerSPSelf()
	{ return null; }
	
	public EntityPlayerSP getPlayer()
	{ return isOnline() ? FTBLibClient.getPlayerSP(getUUID()) : null; }
	
	public Rank getRank()
	{ return Ranks.PLAYER; }
	
	public void receiveInfo(FastList<IChatComponent> info)
	{
		clientInfo.clear();
		clientInfo.addAll(info);
		new EventLMPlayerClient.CustomInfo(this, clientInfo).post();
	}
	
	public void readFromNet(ByteIOStream io, boolean self)
	{
		isOnline = io.readBoolean();
		
		friends.clear();
		friends.addAll(io.readIntArray(ByteCount.SHORT));
		
		IntList otherFriends = IntList.asList(io.readIntArray(ByteCount.SHORT));
		
		for(LMPlayerClient p : world.playerMap)
		{
			if(!p.equalsPlayer(this))
			{
				p.friends.clear();
				if(otherFriends.contains(p.playerID))
				{
					p.friends.add(playerID);
					otherFriends.removeValue(p.playerID);
				}
			}
		}
		
		commonPublicData = LMNBTUtils.readTag(io);
	}
	
	public void onReloaded()
	{
		cachedBadge = null;
	}
}