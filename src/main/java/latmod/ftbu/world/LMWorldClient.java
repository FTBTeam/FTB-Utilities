package latmod.ftbu.world;

import java.io.File;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.MessageLM;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.lib.ByteIOStream;
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
	
	public void readDataFromNet(ByteIOStream io, boolean first)
	{
		if(first)
		{
			players.clear();
			
			int psize = io.readInt();
			
			for(int i = 0; i < psize; i++)
			{
				int id = io.readInt();
				UUID uuid = io.readUUID();
				String name = io.readString();
				players.add(new LMPlayerClient(LMWorldClient.inst, id, new GameProfile(uuid, name)));
			}
		}
		
		NBTTagCompound tag = MessageLM.readTag(io);
		
		if(first)
		{
			UUID selfID = FTBLibClient.mc.thePlayer == null ? null : FTBLibClient.mc.thePlayer.getUniqueID();
			
			NBTTagList list = tag.getTagList("P", LMNBTUtils.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				LMPlayer p = LMWorldClient.inst.getPlayer(tag1.getInteger("PID"));
				if(p != null) p.toPlayerSP().readFromNet(tag1, selfID != null && p.getUUID().equals(selfID));
			}
			
			for(int i = 0; i < players.size(); i++)
				new EventLMPlayerClient.DataLoaded(players.get(i).toPlayerSP()).post();
		}
		
		customCommonData = tag.getCompoundTag("C");
		settings.readFromNBT(tag.getCompoundTag("S"), false);
	}
}