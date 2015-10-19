package latmod.ftbu.world;

import java.io.File;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import ftb.lib.mod.FTBLib;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.util.LMNBTUtils;
import latmod.ftbu.util.client.LatCoreMCClient;
import net.minecraft.nbt.*;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld // LMWorldServer
{
	public static LMWorldClient inst = null;
	public final int clientPlayerID;
	public final File clientDataFolder;
	public LMPlayerClient clientPlayer = null;
	
	public LMWorldClient(UUID id, String ids, int i)
	{
		super(Side.CLIENT, id, ids);
		clientPlayerID = i;
		clientDataFolder = new File(FTBLib.folderLocal, "client/" + worldIDS);
	}
	
	public World getMCWorld()
	{ return LatCoreMCClient.mc.theWorld; }
	
	public LMWorldClient getClientWorld()
	{ return this; }
	
	public LMPlayerClient getPlayer(Object o)
	{
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerSP();
	}
	
	public void readDataFromNet(NBTTagCompound tag, boolean first)
	{
		if(first)
		{
			players.clear();
			
			NBTTagList list = tag.getTagList("PLIST", LMNBTUtils.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				LMPlayerClient p = new LMPlayerClient(this, tag1.getInteger("PID"), new GameProfile(new UUID(tag1.getLong("MID"), tag1.getLong("LID")), tag1.getString("N")));
				p.readFromNet(tag1, p.playerID == clientPlayerID);
				players.add(p);
			}
			
			clientPlayer = LMWorldClient.inst.getPlayer(clientPlayerID);
			
			for(int i = 0; i < players.size(); i++)
				new EventLMPlayerClient.DataLoaded(players.get(i).toPlayerSP()).post();
		}
		
		customCommonData = tag.getCompoundTag("C");
		settings.readFromNBT(tag.getCompoundTag("CFG"), false);
	}
}