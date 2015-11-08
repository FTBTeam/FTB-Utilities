package latmod.ftbu.world;

import java.io.File;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.EventLMPlayerClient;
import net.minecraft.nbt.*;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld // LMWorldServer
{
	public static LMWorldClient inst = null;
	public final int clientPlayerID;
	public final File clientDataFolder;
	
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
	
	public LMPlayerClient getClientPlayer()
	{ return getPlayer(FTBLibClient.mc.thePlayer.getGameProfile().getId()); }
	
	public void readDataFromNet(NBTTagCompound tag, boolean first)
	{
		if(first)
		{
			UUID selfID = FTBLibClient.mc.thePlayer == null ? UUID.randomUUID() : FTBLibClient.mc.thePlayer.getUniqueID();
			
			players.clear();
			
			NBTTagList list = tag.getTagList("PLIST", LMNBTUtils.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				LMPlayerClient p = new LMPlayerClient(this, tag1.getInteger("PID"), new GameProfile(new UUID(tag1.getLong("MID"), tag1.getLong("LID")), tag1.getString("N")));
				p.readFromNet(tag1, selfID != null && p.getUUID().equals(selfID));
				players.add(p);
			}
			
			for(int i = 0; i < players.size(); i++)
				new EventLMPlayerClient.DataLoaded(players.get(i).toPlayerSP()).post();
		}
		
		customCommonData = tag.getCompoundTag("C");
		settings.readFromNBT(tag.getCompoundTag("CFG"), false);
	}
}