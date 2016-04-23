package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.utils.api.EventLMPlayerClient;
import latmod.lib.*;
import net.minecraft.world.World;

import java.util.*;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld // LMWorldServer
{
	public static LMWorldClient inst = null;
	
	public final int clientPlayerID;
	public final Map<Integer, LMPlayerClient> playerMap;
	public LMPlayerClientSelf clientPlayer = null;
	
	public LMWorldClient(int i)
	{
		super(Side.CLIENT);
		clientPlayerID = i;
		playerMap = new HashMap<>();
	}
	
	@Override
	public Map<Integer, ? extends LMPlayer> playerMap()
	{ return playerMap; }
	
	@Override
	public World getMCWorld()
	{ return FTBLibClient.mc.theWorld; }
	
	@Override
	public LMWorldClient getClientWorld()
	{ return this; }
	
	@Override
	public LMPlayerClient getPlayer(Object o)
	{
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerSP();
	}
	
	public void readDataFromNet(ByteIOStream io, boolean first)
	{
		if(first)
		{
			playerMap.clear();
			
			GameProfile gp = FTBLibClient.mc.getSession().func_148256_e();
			clientPlayer = new LMPlayerClientSelf(this, clientPlayerID, gp);
			
			int psize = io.readInt();
			
			for(int i = 0; i < psize; i++)
			{
				int id = io.readInt();
				UUID uuid = io.readUUID();
				String name = io.readUTF();
				
				if(id == clientPlayerID) playerMap.put(id, clientPlayer);
				else playerMap.put(id, new LMPlayerClient(this, id, new GameProfile(uuid, name)));
			}
			
			FTBLib.dev_logger.info("Client player ID: " + clientPlayerID);
			
			int[] onlinePlayers = io.readIntArray(ByteCount.INT);
			
			for(int i = 0; i < onlinePlayers.length; i++)
			{
				LMPlayerClient p = playerMap.get(onlinePlayers[i]).toPlayerSP();
				p.readFromNet(io, p.getPlayerID() == clientPlayerID);
				new EventLMPlayerClient.DataLoaded(p).post();
			}
			
			clientPlayer.readFromNet(io, true);
			new EventLMPlayerClient.DataLoaded(clientPlayer).post();
		}
		
		customCommonData.readFromNBT(LMNBTUtils.readTag(io), false);
		
		settings.readFromNet(io);
	}
}