package latmod.core.mod;
import java.io.*;

import latmod.core.*;
import latmod.core.event.*;
import latmod.core.net.*;
import latmod.core.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LCEventHandler
{
	public static final String ACTION_OPEN_FRIENDS_GUI = "OpenFriendsGUI";
	
	public static final LCEventHandler instance = new LCEventHandler();
	
	@SubscribeEvent
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		if(LCConfig.General.checkUpdates)
			ThreadCheckVersions.init(e.player, false);
		
		LMPlayer p = LMPlayer.getPlayer(e.player);
		
		boolean first = p != null && !p.isOld;
		boolean sendAll = false;
		
		String cmdName = e.player.getCommandSenderName();
		
		if(p == null)
		{
			first = true;
			p = new LMPlayer(LMDataLoader.nextPlayerID(), e.player.getUniqueID(), cmdName);
			LMPlayer.map.put(p.playerID, p);
		}
		else
		{
			if(!p.username.equals(cmdName))
			{
				p = new LMPlayer(p.playerID, e.player.getUniqueID(), cmdName);
				LMPlayer.map.put(p.playerID, p);
				sendAll = true;
			}
		}
		
		p.isOld = !first;
		p.setOnline(true);
		
		updateAllData(sendAll ? null : (EntityPlayerMP)e.player);
		p.sendUpdate(LMPlayer.ACTION_LOGGED_IN);
		
		p.isOld = true;
		
		e.player.refreshDisplayName();
	}
	
	@SubscribeEvent
	public void playerLoggedOut(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.player);
		
		if(p != null)
		{
			p.setOnline(false);
			
			for(int i = 0; i < 4; i++)
				p.lastArmor[i] = e.player.inventory.armorInventory[i];
			p.lastArmor[4] = e.player.inventory.getCurrentItem();
			
			p.sendUpdate(LMPlayer.ACTION_LOGGED_OUT);
		}
	}
	
	@SubscribeEvent
	public void worldLoaded(WorldEvent.Load e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			File f0 = new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat");
			
			if(f0.exists())
			{
				LatCoreMC.logger.info("Old LatCoreMC.dat found");
				
				try
				{
					NBTTagCompound tag = NBTHelper.readMap(new FileInputStream(f0));
					LMDataLoader.Old.readFromNBT(tag);
					
					for(int i = 0; i < LMPlayer.map.values.size(); i++)
						LMPlayer.map.values.get(i).setOnline(false);
					
					f0.delete();
					
					LatCoreMC.logger.info("Old LatCoreMC.dat loaded");
				}
				catch(Exception ex)
				{
					LatCoreMC.logger.warn("Error occured while loading LatCoreMC.dat!");
					ex.printStackTrace();
				}
				
				worldSaved(new WorldEvent.Save(e.world));
			}
			else
			{
				LoadLMDataEvent e1 = new LoadLMDataEvent(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/"), EventLM.Phase.PRE);
				e1.post();
				LMGamerules.load(e1);
				
				NBTTagCompound players = NBTHelper.readMap(e1.getFile("LMPlayers.dat"));
				if(players != null) LMDataLoader.readPlayersFromNBT(players);
				
				for(int i = 0; i < LMPlayer.map.values.size(); i++)
					LMPlayer.map.values.get(i).setOnline(false);
				
				NBTTagCompound common = NBTHelper.readMap(e1.getFile("CommonData.dat"));
				if(common != null)
				{
					new LoadLMDataEvent.CommonData(common).post();
					LMDataLoader.lastPlayerID = common.getInteger("LastPlayerID");
				}
				
				new LoadLMDataEvent(e1.latmodFolder, EventLM.Phase.POST).post();
				
				LatCoreMC.logger.info("LatCoreMC data loaded");
			}
		}
	}
	
	@SubscribeEvent
	public void worldSaved(WorldEvent.Save e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			SaveLMDataEvent e1 = new SaveLMDataEvent(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/"));
			e1.post();
			LMGamerules.save(e1);
			
			NBTTagCompound common = new NBTTagCompound();
			new SaveLMDataEvent.CommonData(common).post();
			common.setInteger("LastPlayerID", LMDataLoader.lastPlayerID);
			NBTHelper.writeMap(e1.getFile("CommonData.dat"), common);
			
			NBTTagCompound players = new NBTTagCompound();
			LMDataLoader.writePlayersToNBT(players);
			NBTHelper.writeMap(e1.getFile("LMPlayers.dat"), players);
			
			// Export player list //
			
			try
			{
				FastList<String> l = new FastList<String>();
				FastList<Integer> list = new FastList<Integer>();
				list.addAll(LMPlayer.map.keys);
				list.sort(null);
				
				for(int i = 0; i < list.size(); i++)
				{
					LMPlayer p = LMPlayer.getPlayer(list.get(i));
					
					String id = LatCore.fillString("" + p.playerID, ' ', 6);
					String u = LatCore.fillString(p.username, ' ', 21);
					String s = "" + p.uuid;
					
					l.add(id + u + s);
				}
				
				LatCore.saveFile(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/LMPlayers.txt"), l);
			}
			catch(Exception ex)
			{
				LatCoreMC.logger.warn("Error occured while saving LatCoreMC.dat!");
				ex.printStackTrace();
			}
		}
	}
	
	public void updateAllData(EntityPlayerMP ep)
	{
		if(ep != null) MessageLM.NET.sendTo(new MessageUpdateAllData(), ep);
		else MessageLM.NET.sendToAll(new MessageUpdateAllData());
	}
	
	@SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e)
    {
		if(e.modID.equalsIgnoreCase(LC.MOD_ID))
			LCConfig.instance.load();
	}
}