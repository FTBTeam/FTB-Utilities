package latmod.core.mod;

import java.util.UUID;

import latmod.core.*;
import latmod.core.event.*;
import net.minecraft.nbt.*;

public class LMDataLoader
{
	private static int lastPlayerID = 0;
	
	public static int nextPlayerID()
	{ return ++lastPlayerID; }
	
	public static void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound players = new NBTTagCompound();
		
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayer p = LMPlayer.map.values.get(i);
			p.writeToNBT(tag1);
			new LMPlayerEvent.DataSaved(p).post();
			tag1.setString("UUID", p.uuid.toString());
			tag1.setString("Name", p.username);
			
			players.setTag(p.playerID + "", tag1);
		}
		
		tag.setTag("Players", players);
		tag.setInteger("LastPlayerID", lastPlayerID);
		
		LMGamerules.writeToNBT(tag);
		new SaveCustomLMDataEvent(tag).post();
	}
	
	public static void readFromNBT(NBTTagCompound tag)
	{
		LMPlayer.map.clear();
		lastPlayerID = tag.getInteger("LastPlayerID");
		new LoadCustomLMDataEvent(EventLM.Phase.PRE, tag).post();
		FastMap<Integer, NBTTagCompound> playerData = new FastMap<Integer, NBTTagCompound>();
		FastMap<String, NBTTagCompound> map = NBTHelper.toFastMapWithType(tag.getCompoundTag("Players"));
		
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
			p.readFromNBT(playerData.get(Integer.valueOf(p.playerID)));
			new LMPlayerEvent.DataLoaded(p).post();
		}
		
		LMGamerules.readFromNBT(tag);
		new LoadCustomLMDataEvent(EventLM.Phase.POST, tag).post();
	}
	
	public static class Old
	{
		public static void readFromNBT(NBTTagCompound tag)
		{
			LMPlayer.map.clear();
			lastPlayerID = 0;
			new LoadCustomLMDataEvent(EventLM.Phase.PRE, tag).post();
			FastMap<Integer, NBTTagCompound> playerData = new FastMap<Integer, NBTTagCompound>();
			FastMap<String, NBTTagCompound> map = NBTHelper.toFastMapWithType(tag.getCompoundTag("Players"));
			
			for(int i = 0; i < map.size(); i++)
			{
				NBTTagCompound tag1 = map.values.get(i);
				LMPlayer p = new LMPlayer(nextPlayerID(), UUID.fromString(tag1.getString("UUID")), map.keys.get(i));
				LMPlayer.map.put(p.playerID, p);
				playerData.put(Integer.valueOf(p.playerID), tag1);
			}
			
			for(int i = 0; i < LMPlayer.map.values.size(); i++)
			{
				LMPlayer p = LMPlayer.map.values.get(i);
				readPlayerFromNBT(p, playerData.get(Integer.valueOf(p.playerID)));
				new LMPlayerEvent.DataLoaded(p).post();
			}
			
			LMGamerules.readFromNBT(tag);
			new LoadCustomLMDataEvent(EventLM.Phase.POST, tag).post();
		}
		
		private static void readPlayerFromNBT(LMPlayer player, NBTTagCompound tag)
		{
			player.setOnline(tag.getBoolean("On"));
			
			player.friends.clear();
			player.groups.clear();
			
			if(tag.hasKey("Friends"))
			{
				FastMap<String, NBTBase.NBTPrimitive> map = NBTHelper.toFastMapWithType(tag.getCompoundTag("Friends"));
				
				for(int i = 0; i < map.size(); i++)
				{
					if(map.values.get(i).func_150290_f() == 1)
					{
						LMPlayer p = LMPlayer.getPlayer(map.keys.get(i));
						if(p != null) player.friends.add(p);
					}
				}
				
				tag.removeTag("Friends");
				LatCoreMC.logger.info("Found old LMFriends");
			}
			else
			{
				NBTTagCompound tag1 = tag.getCompoundTag("Groups");
				
				NBTTagList fl = (NBTTagList)tag1.getTag("Friends");
				
				if(fl != null) for(int j = 0; j < fl.tagCount(); j++)
				{
					LMPlayer p = LMPlayer.getPlayer(fl.getStringTagAt(j));
					if(p != null) player.friends.add(p);
				}
			}
			
			player.customData = tag.getCompoundTag("CustomData");
			
			if(player.customData.hasKey("IsOld"))
			{
				tag.setBoolean("Old", player.customData.getBoolean("IsOld"));
				player.customData.removeTag("IsOld");
			}
			
			player.isOld = tag.getBoolean("Old");
		}
	}
}