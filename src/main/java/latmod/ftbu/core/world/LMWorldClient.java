package latmod.ftbu.core.world;

import java.io.File;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.*;
import net.minecraft.nbt.*;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld<LMPlayerClient>
{
	public static LMWorldClient inst = null;
	public final int clientPlayerID;
	public final boolean hasServer;
	public final File clientDataFolder;
	public LMPlayerClient clientPlayer = null;
	
	public LMWorldClient(UUID id, String ids, int i)
	{
		super(Side.CLIENT, id, ids);
		clientPlayerID = i;
		hasServer = clientPlayerID > 0;
		clientDataFolder = new File(LatCoreMC.latmodFolder, "client/" + worldIDS);
	}
	
	public void readPlayersFromNet(NBTTagCompound tag)
	{
		players.clear();
		
		NBTTagList list = tag.getTagList("Players", LMNBTUtils.MAP);
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			LMPlayerClient p = new LMPlayerClient(this, tag1.getInteger("PID"), new GameProfile(new UUID(tag1.getLong("MID"), tag1.getLong("LID")), tag1.getString("N")));
			p.readFromNet(tag1, p.playerID == clientPlayerID);
			players.add(p);
		}
		
		clientPlayer = LMWorldClient.inst.getPlayer(clientPlayerID);
		
		for(int i = 0; i < players.size(); i++)
			players.get(i).onPostLoaded();
	}
}