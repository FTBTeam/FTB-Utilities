package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.*;
import ftb.lib.LMNBTUtils;
import ftb.lib.api.client.FTBLibClient;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.ranks.*;
import latmod.lib.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.*;

import java.util.*;

@SideOnly(Side.CLIENT)
public class LMPlayerClient extends LMPlayer // LMPlayerServer // LMPlayerClientSelf
{
	public final LMWorldClient world;
	public final List<IChatComponent> clientInfo;
	public boolean isOnline;
	
	public LMPlayerClient(LMWorldClient w, int i, GameProfile gp)
	{
		super(i, gp);
		world = w;
		clientInfo = new ArrayList<>();
		isOnline = false;
	}
	
	public ResourceLocation getSkin()
	{ return FTBLibClient.getSkinTexture(getProfile().getName()); }
	
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
	{ return isOnline() ? FTBLibClient.getPlayerSP(getProfile().getId()) : null; }
	
	public Rank getRank()
	{ return Ranks.PLAYER; }
	
	public void receiveInfo(List<IChatComponent> info)
	{
		clientInfo.clear();
		clientInfo.addAll(info);
		new EventLMPlayerClient.CustomInfo(this, clientInfo).post();
	}
	
	public void readFromNet(ByteIOStream io, boolean self)
	{
		isOnline = io.readBoolean();
		renderBadge = io.readBoolean();
		
		friends.clear();
		friends.addAll(io.readIntArray(ByteCount.SHORT));
		
		IntList otherFriends = IntList.asList(io.readIntArray(ByteCount.SHORT));
		
		for(LMPlayerClient p : world.playerMap.values())
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
}