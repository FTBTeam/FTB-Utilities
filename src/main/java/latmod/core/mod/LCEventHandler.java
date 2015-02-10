package latmod.core.mod;
import java.io.*;
import java.util.UUID;

import latmod.core.*;
import latmod.core.event.*;
import latmod.core.net.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.Side;

public class LCEventHandler
{
	public static final String ACTION_OPEN_FRIENDS_GUI = "OpenFriendsGUI";
	
	public static final LCEventHandler instance = new LCEventHandler();
	private static int nextPlayerID = 0;
	
	@SubscribeEvent
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("UUID: " + e.player.getUniqueID() + ", " + e.player);
		
		if(LCConfig.General.checkUpdates)
			ThreadCheckVersions.init(e.player, false);
		
		LMPlayer p = LMPlayer.getPlayer(e.player);
		
		boolean first = p != null && !p.isOld;
		
		if(p == null)
		{
			first = true;
			p = new LMPlayer(++nextPlayerID, e.player.getUniqueID(), e.player.getCommandSenderName());
			LMPlayer.map.put(p.playerID, p);
		}
		
		p.isOld = true;
		p.setOnline(true);
		
		updateAllData((EntityPlayerMP)e.player);
		new LMPlayerEvent.LoggedIn(p, Side.SERVER, e.player, first).post();
		p.sendUpdate(LMPlayer.ACTION_LOGGED_IN);
		
		e.player.refreshDisplayName();
	}
	
	@SubscribeEvent
	public void playerLoggedOut(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.player);
		
		if(p != null)
		{
			p.setOnline(false);
			p.sendUpdate(LMPlayer.ACTION_LOGGED_OUT);
			new LMPlayerEvent.LoggedOut(p, Side.SERVER, e.player).post();
		}
	}
	
	@SubscribeEvent
	public void worldLoaded(WorldEvent.Load e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			File f = new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat");
			
			if(f.exists())
			{
				try
				{
					NBTTagCompound tag = NBTHelper.readMap(new FileInputStream(f));
					loadAllData(tag);
					
					for(int i = 0; i < LMPlayer.map.values.size(); i++)
						LMPlayer.map.values.get(i).setOnline(false);
					
					LatCoreMC.logger.info("LatCoreMC.dat loaded");
				}
				catch(Exception ex)
				{
					LatCoreMC.logger.warn("Error occured while loading LatCoreMC.dat!");
					ex.printStackTrace();
				}
			}
			else LatCoreMC.logger.info("LatCoreMC.dat not found");
			
			LMGamerules.postInit();
		}
	}
	
	public void loadAllData(NBTTagCompound tag)
	{
		LMPlayer.map.clear();
		
		nextPlayerID = tag.getInteger("NextPlayerID");
		
		new LoadCustomLMDataEvent(EventLM.Phase.PRE, tag).post();
		
		FastMap<Integer, NBTTagCompound> playerData = new FastMap<Integer, NBTTagCompound>();
		
		if(tag.hasKey("Players"))
		{
			FastMap<String, NBTTagCompound> map = NBTHelper.toFastMapWithType(tag.getCompoundTag("Players"));
			
			for(int i = 0; i < map.size(); i++)
			{
				NBTTagCompound tag1 = map.values.get(i);
				LMPlayer p = new LMPlayer(++nextPlayerID, UUID.fromString(tag1.getString("UUID")), map.keys.get(i));
				LMPlayer.map.put(p.playerID, p);
				playerData.put(p.playerID, tag1);
			}
			
			LatCoreMC.logger.info("Found Old LMPlayers");
		}
		else
		{
			FastMap<String, NBTTagCompound> map = NBTHelper.toFastMapWithType(tag.getCompoundTag("LMPlayers"));
			
			for(int i = 0; i < map.size(); i++)
			{
				int id = Integer.parseInt(map.keys.get(i));
				NBTTagCompound tag1 = map.values.get(i);
				LMPlayer p = new LMPlayer(id, UUID.fromString(tag1.getString("UUID")), tag1.getString("Name"));
				LMPlayer.map.put(p.playerID, p);
				playerData.put(p.playerID, tag1);
			}
		}
		
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			LMPlayer p = LMPlayer.map.values.get(i);
			p.readFromNBT(playerData.get(p.playerID));
			new LMPlayerEvent.DataLoaded(p).post();
		}
		
		LMGamerules.readFromNBT(tag);
		
		new LoadCustomLMDataEvent(EventLM.Phase.POST, tag).post();
	}
	
	@SubscribeEvent
	public void worldSaved(WorldEvent.Save e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			File f = LatCore.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat"));
			
			try
			{
				NBTTagCompound tag = new NBTTagCompound();
				saveAllData(tag);
				NBTHelper.writeMap(new FileOutputStream(f), tag);
			}
			catch(Exception ex)
			{
				LatCoreMC.logger.warn("Error occured while saving LatCoreMC.dat!");
				ex.printStackTrace();
			}
		}
	}
	
	public void saveAllData(NBTTagCompound tag)
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
		
		tag.setTag("LMPlayers", players);
		tag.setInteger("NextPlayerID", nextPlayerID);
		
		LMGamerules.writeToNBT(tag);
		new SaveCustomLMDataEvent(tag).post();
	}
	
	public void updateAllData(EntityPlayerMP ep)
	{
		if(ep != null) MessageLM.NET.sendTo(new MessageUpdateLMData(), ep);
		else MessageLM.NET.sendToAll(new MessageUpdateLMData());
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void playerName(PlayerEvent.NameFormat e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.entityPlayer);
		if(p != null && p.hasCustomName())
			e.displayname = p.getDisplayName();
	}
	
	@SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e)
    {
		if(e.modID.equalsIgnoreCase(LC.MOD_ID))
			LCConfig.instance.load();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLMKeyEvent(LMKeyEvent e)
	{
		if(e.side.isServer() && e.keys.contains(Key.CRTL))
		{
			MessageLM.NET.sendTo(new MessageCustomServerAction(LCEventHandler.ACTION_OPEN_FRIENDS_GUI, null), (EntityPlayerMP)e.player);
			e.setCanceled(true);
		}
	}
}