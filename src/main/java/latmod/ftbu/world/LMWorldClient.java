package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.lib.*;
import net.minecraft.world.World;

import java.io.File;
import java.util.*;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld // LMWorldServer
{
	public static LMWorldClient inst = null;
	
	public final int clientPlayerID;
	public final File clientDataFolder;
	public final HashMap<Integer, LMPlayerClient> playerMap;
	public LMPlayerClientSelf clientPlayer = null;
	
	public LMWorldClient(int i)
	{
		super(Side.CLIENT);
		clientPlayerID = i;
		clientDataFolder = new File(FTBLib.folderLocal, "client/" + FTBWorld.client.getWorldIDS());
		playerMap = new HashMap<>();
	}
	
	public HashMap<Integer, ? extends LMPlayer> playerMap()
	{ return playerMap; }
	
	public World getMCWorld()
	{ return FTBLibClient.mc.theWorld; }
	
	public LMWorldClient getClientWorld()
	{ return this; }
	
	public LMPlayerClient getPlayer(Object o)
	{
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerSP();
	}
	
	public LMPlayerClientSelf getClientPlayer()
	{ return clientPlayer; }
	
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
			
			FTBLib.dev_logger.info("Client player ID: " + clientPlayerID + ", " + getPlayer(clientPlayerID).getClass());
			
			int[] onlinePlayers = io.readIntArray(ByteCount.INT);
			
			for(int i = 0; i < onlinePlayers.length; i++)
			{
				LMPlayerClient p = playerMap.get(onlinePlayers[i]).toPlayerSP();
				p.readFromNet(io, p.playerID == clientPlayerID);
				new EventLMPlayerClient.DataLoaded(p).post();
			}
		}
		
		settings.readFromNet(io);
		
		try { customCommonData.read(io); }
		catch(Exception ex) {}
	}
}