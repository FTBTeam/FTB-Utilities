package latmod.core.mod;
import java.io.*;

import latmod.core.*;
import latmod.core.event.*;
import latmod.core.net.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.Side;

public class LCEventHandler
{
	public static final String ACTION_OPEN_FRIENDS_GUI = "OpenFriendsGUI";
	
	public static final LCEventHandler instance = new LCEventHandler();
	
	@SubscribeEvent
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("UUID: " + e.player.getUniqueID() + ", " + e.player);
		
		if(LCConfig.General.checkUpdates)
			ThreadCheckVersions.init(e.player, false);
		
		LMPlayer p = LMPlayer.getPlayer(e.player);
		
		boolean first = p != null && !p.isOld;
		
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
			}
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
				LatCoreMC.logger.info("Old LatCoreMC.dat found");
				
				try
				{
					NBTTagCompound tag = NBTHelper.readMap(new FileInputStream(f));
					LMDataLoader.Old.readFromNBT(tag);
					
					for(int i = 0; i < LMPlayer.map.values.size(); i++)
						LMPlayer.map.values.get(i).setOnline(false);
					
					f.delete();
					
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
				f = new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/LatCoreMC.dat");
				
				if(f.exists())
				{
					try
					{
						NBTTagCompound tag = NBTHelper.readMap(new FileInputStream(f));
						LMDataLoader.readFromNBT(tag);
						
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
			}
			
			LMGamerules.postInit();
		}
	}
	
	@SubscribeEvent
	public void worldSaved(WorldEvent.Save e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			File f = LatCore.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/LatCoreMC.dat"));
			
			try
			{
				NBTTagCompound tag = new NBTTagCompound();
				LMDataLoader.writeToNBT(tag);
				NBTHelper.writeMap(new FileOutputStream(f), tag);
				
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
					if(p.hasCustomName()) s += " (" + p.getDisplayName() + ")";
					
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
}