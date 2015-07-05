package latmod.ftbu.core.world;

import java.util.UUID;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.FastMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.FakePlayer;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld
{
	public final FastMap<Integer, LMPlayerClient> players;
	public int clientPlayerID;
	
	public LMWorldClient(UUID id)
	{
		super(Side.CLIENT, id);
		players = new FastMap<Integer, LMPlayerClient>();
	}
	
	public FastMap<Integer, ? extends LMPlayer> getPlayers()
	{ return players; }
	
	public LMPlayer getClientPlayer()
	{ return getPlayer(clientPlayerID); }
	
	public LMPlayerClient getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		else if(o instanceof Integer || o instanceof LMPlayer)
		{
			int h = o.hashCode();
			return (h <= 0) ? null : players.get(h);
		}
		else if(o.getClass() == UUID.class)
		{
			UUID id = (UUID)o;
			
			for(int i = 0; i < players.size(); i++)
			{
				LMPlayerClient p = players.values.get(i);
				if(p.getUUID().equals(id)) return p;
			}
		}
		else if(o instanceof EntityPlayer)
			return getPlayer(((EntityPlayer)o).getUniqueID());
		else if(o instanceof String)
		{
			String s = o.toString();
			
			if(s == null || s.isEmpty()) return null;
			
			for(int i = 0; i < players.size(); i++)
			{
				LMPlayerClient p = players.values.get(i);
				if(p.getName().equalsIgnoreCase(s)) return p;
			}
			
			return getPlayer(LatCoreMC.getUUIDFromString(s));
		}
		
		return null;
	}
	
	public void readPlayersFromNet(NBTTagCompound tag)
	{
		players.clear();
		
		NBTTagList list = tag.getTagList("Players", NBTHelper.MAP);
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			LMPlayerClient p = new LMPlayerClient(this, tag1.getInteger("PID"), new GameProfile(new UUID(tag1.getLong("MID"), tag1.getLong("LID")), tag1.getString("N")));
			p.readFromNet(tag1);
			players.put(p.playerID, p);
		}
		
		for(int i = 0; i < players.values.size(); i++)
			players.values.get(i).onPostLoaded();
	}
}