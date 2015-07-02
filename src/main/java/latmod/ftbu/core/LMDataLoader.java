package latmod.ftbu.core;

import java.util.UUID;

import latmod.ftbu.core.event.LMPlayerEvent;
import latmod.ftbu.core.util.FastMap;
import net.minecraft.nbt.*;

import com.mojang.authlib.GameProfile;

public class LMDataLoader
{
	public static int lastPlayerID = 0;
	
	public static final int nextPlayerID()
	{ return ++lastPlayerID; }
	
	public static void writeNetPlayersToNBT(NBTTagCompound tag)
	{
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayer p = LMPlayer.map.values.get(i);
			p.writeToNBT(tag1, false);
			new LMPlayerEvent.DataSaved(p).post();
			tag1.setLong("MID", p.getUUID().getMostSignificantBits());
			tag1.setLong("LID", p.getUUID().getLeastSignificantBits());
			tag1.setString("Name", p.getName());
			tag1.setInteger("PID", p.playerID);
			
			list.appendTag(tag1);
		}
		
		tag.setTag("Players", list);
	}
	
	public static void readNetPlayersFromNBT(NBTTagCompound tag)
	{
		LMPlayer.map.clear();
		
		NBTTagList list = tag.getTagList("Players", NBTHelper.MAP);
		FastMap<Integer, NBTTagCompound> playerData = new FastMap<Integer, NBTTagCompound>();
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			LMPlayer p = new LMPlayer(tag1.getInteger("PID"), new GameProfile(new UUID(tag1.getLong("MID"), tag1.getLong("LID")), tag1.getString("Name")));
			LMPlayer.map.put(p.playerID, p);
			playerData.put(Integer.valueOf(p.playerID), tag1);
		}
		
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			LMPlayer p = LMPlayer.map.values.get(i);
			p.readFromNBT(playerData.get(Integer.valueOf(p.playerID)), false);
			new LMPlayerEvent.DataLoaded(p).post();
		}
	}
	
	public static void writeSavePlayersToNBT(NBTTagCompound tag)
	{
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayer p = LMPlayer.map.values.get(i);
			p.writeToNBT(tag1, true);
			new LMPlayerEvent.DataSaved(p).post();
			tag1.setString("UUID", p.uuidString);
			tag1.setString("Name", p.getName());
			
			tag.setTag(p.playerID + "", tag1);
		}
	}
	
	public static void readSavePlayersFromNBT(NBTTagCompound tag)
	{
		LMPlayer.map.clear();
		
		FastMap<Integer, NBTTagCompound> playerData = new FastMap<Integer, NBTTagCompound>();
		FastMap<String, NBTTagCompound> map = NBTHelper.toFastMapWithType(tag);
		
		for(int i = 0; i < map.size(); i++)
		{
			int id = Integer.parseInt(map.keys.get(i));
			NBTTagCompound tag1 = map.values.get(i);
			LMPlayer p = new LMPlayer(id, new GameProfile(LatCoreMC.getUUIDFromString(tag1.getString("UUID")), tag1.getString("Name")));
			LMPlayer.map.put(p.playerID, p);
			playerData.put(Integer.valueOf(p.playerID), tag1);
		}
		
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			LMPlayer p = LMPlayer.map.values.get(i);
			p.readFromNBT(playerData.get(Integer.valueOf(p.playerID)), true);
			new LMPlayerEvent.DataLoaded(p).post();
		}
	}
}