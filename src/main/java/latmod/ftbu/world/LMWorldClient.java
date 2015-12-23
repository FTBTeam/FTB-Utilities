package latmod.ftbu.world;

import java.io.File;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.lib.*;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld // LMWorldServer
{
	public static LMWorldClient inst = null;
	public final int clientPlayerID;
	public final File clientDataFolder;
	public LMPlayerClientSelf clientPlayer = null;
	
	public LMWorldClient(int i)
	{
		super(Side.CLIENT);
		clientPlayerID = i;
		clientDataFolder = new File(FTBLib.folderLocal, "client/" + FTBWorld.client.getWorldIDS());
	}
	
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
			players.clear();
			
			GameProfile gp = FTBLibClient.mc.getSession().func_148256_e();
			clientPlayer = new LMPlayerClientSelf(this, clientPlayerID, gp);
			
			int psize = io.readInt();
			
			for(int i = 0; i < psize; i++)
			{
				int id = io.readInt();
				UUID uuid = io.readUUID();
				String name = io.readUTF();
				
				if(id == clientPlayerID) players.add(clientPlayer);
				else players.add(new LMPlayerClient(this, id, new GameProfile(uuid, name)));
			}
			
			FTBLib.dev_logger.info("Client player ID: " + clientPlayerID + ", " + getPlayer(clientPlayerID).getClass());
			
			int[] onlinePlayers = io.readIntArray(ByteCount.INT);
			
			for(int i = 0; i < onlinePlayers.length; i++)
			{
				LMPlayerClient p = players.get(onlinePlayers[i]).toPlayerSP();
				p.readFromNet(io, p.playerID == clientPlayerID);
				new EventLMPlayerClient.DataLoaded(p).post();
			}
		}
		
		settings.readFromNet(io);
		customCommonData.read(io);
	}
}