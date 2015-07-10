package latmod.ftbu.core.world;

import java.util.UUID;

import latmod.ftbu.core.NBTHelper;
import net.minecraft.nbt.*;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld<LMPlayerClient>
{
	public int clientPlayerID;
	
	public LMWorldClient(UUID id)
	{
		super(Side.CLIENT, id);
	}
	
	public LMPlayerClient getClientPlayer()
	{ return getPlayer(clientPlayerID); }
	
	public void readPlayersFromNet(NBTTagCompound tag)
	{
		players.clear();
		
		NBTTagList list = tag.getTagList("Players", NBTHelper.MAP);
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			LMPlayerClient p = new LMPlayerClient(this, tag1.getInteger("PID"), new GameProfile(new UUID(tag1.getLong("MID"), tag1.getLong("LID")), tag1.getString("N")));
			p.readFromNet(tag1);
			players.add(p);
		}
		
		for(int i = 0; i < players.size(); i++)
			players.get(i).onPostLoaded();
	}
}