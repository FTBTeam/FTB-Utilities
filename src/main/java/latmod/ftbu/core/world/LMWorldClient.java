package latmod.ftbu.core.world;

import java.util.UUID;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import net.minecraft.nbt.*;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld<LMPlayerClient>
{
	public static LMWorldClient inst = null;
	public LMPlayerClient clientPlayer = null;
	public final boolean hasServer;
	
	public LMWorldClient(UUID id, boolean b)
	{
		super(Side.CLIENT, id);
		hasServer = b;
		LatCoreMC.logger.info("Created LMWorldClient " + worldIDS + " with UUID " + worldID);
	}
	
	public void readPlayersFromNet(NBTTagCompound tag)
	{
		UUID selfID = LatCoreMCClient.getUUID();
		players.clear();
		
		NBTTagList list = tag.getTagList("Players", LMNBTUtils.MAP);
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			LMPlayerClient p = new LMPlayerClient(this, tag1.getInteger("PID"), new GameProfile(new UUID(tag1.getLong("MID"), tag1.getLong("LID")), tag1.getString("N")));
			p.readFromNet(tag1, p.getUUID().equals(selfID));
			players.add(p);
		}
		
		for(int i = 0; i < players.size(); i++)
			players.get(i).onPostLoaded();
	}
	
	public static class NoServerWorld extends LMWorldClient
	{
		public static final UUID noServerWorldUUID = new UUID(0L, 0L);
		public static String worldIDSNoWorld;
		
		public NoServerWorld()
		{ super(noServerWorldUUID, false); }
		
		protected String getWorldIDS()
		{ return worldIDSNoWorld + ""; }
	}
}