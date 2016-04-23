package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.LMNBTUtils;
import ftb.lib.api.client.FTBLibClient;
import ftb.utils.api.EventLMPlayerClient;
import latmod.lib.ByteCount;
import latmod.lib.ByteIOStream;
import latmod.lib.IntList;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LMPlayerClient extends LMPlayer // LMPlayerServer // LMPlayerClientSelf
{
	public final LMWorldClient world;
	public final List<String> clientInfo;
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
	
	@Override
	public LMWorld getWorld()
	{ return world; }
	
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
	
	public void receiveInfo(List<IChatComponent> info)
	{
		clientInfo.clear();
		
		for(IChatComponent c : info)
		{
			clientInfo.add(c.getFormattedText());
		}
		
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
				if(otherFriends.contains(p.getPlayerID()))
				{
					p.friends.add(getPlayerID());
					otherFriends.removeValue(p.getPlayerID());
				}
			}
		}
		
		commonPublicData = LMNBTUtils.readTag(io);
	}
}