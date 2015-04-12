package latmod.core.mod;

import java.util.UUID;

import latmod.core.*;
import latmod.core.event.LMPlayerEvent;
import latmod.core.util.FastMap;
import net.minecraft.nbt.NBTTagCompound;

public class LMDataLoader
{
	public static int lastPlayerID = 0;
	
	public static final int nextPlayerID()
	{ return ++lastPlayerID; }
	
	public static void writePlayersToNBT(NBTTagCompound tag, boolean server)
	{
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayer p = LMPlayer.map.values.get(i);
			p.writeToNBT(tag1, server);
			new LMPlayerEvent.DataSaved(p).post();
			tag1.setString("UUID", p.uuid.toString());
			tag1.setString("Name", p.username);
			
			tag.setTag(p.playerID + "", tag1);
		}
	}
	
	public static void readPlayersFromNBT(NBTTagCompound tag, boolean server)
	{
		LMPlayer.map.clear();
		
		FastMap<Integer, NBTTagCompound> playerData = new FastMap<Integer, NBTTagCompound>();
		FastMap<String, NBTTagCompound> map = NBTHelper.toFastMapWithType(tag);
		
		for(int i = 0; i < map.size(); i++)
		{
			int id = Integer.parseInt(map.keys.get(i));
			NBTTagCompound tag1 = map.values.get(i);
			LMPlayer p = new LMPlayer(id, UUID.fromString(tag1.getString("UUID")), tag1.getString("Name"));
			LMPlayer.map.put(p.playerID, p);
			playerData.put(Integer.valueOf(p.playerID), tag1);
		}
		
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			LMPlayer p = LMPlayer.map.values.get(i);
			p.readFromNBT(playerData.get(Integer.valueOf(p.playerID)), server);
			new LMPlayerEvent.DataLoaded(p).post();
		}
	}
}