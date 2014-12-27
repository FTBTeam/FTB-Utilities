package latmod.core.mod;
import java.io.*;
import java.util.UUID;

import latmod.core.*;
import latmod.core.event.*;
import latmod.core.net.*;
import latmod.core.tile.IWailaTile;
import latmod.core.waila.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.*;

public class LCEventHandler
{
	public static final String ACTION_PLAYER_JOINED = "PlayerJoined";
	public static final String ACTION_OPEN_URL = "OpenURL";
	public static final String ACTION_OPEN_FRIENDS_GUI = "OpenFriendsGUI";
	
	public static final LCEventHandler instance = new LCEventHandler();
	
	public static final UUID UUID_LatvianModder  = UUID.fromString("5afb9a5b-207d-480e-8879-67bc848f9a8f");
	
	@SubscribeEvent
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		//LatCoreMC.logger.info("UUID: " + id);
		
		boolean first = false;
		
		LMPlayer p = LMPlayer.getPlayer(e.player);
		
		if(p == null)
		{
			p = new LMPlayer(e.player.getUniqueID(), e.player.getCommandSenderName());
			LMPlayer.list.add(p);
		}
		
		if(!p.customData.hasKey("IsOld"))
		{
			p.customData.setBoolean("IsOld", true);
			
			if(p.uuid.equals(UUID_LatvianModder))
			{
				p.setCustomName("LatvianModder");
			}
			
			first = true;
		}
		
		if(LCConfig.General.checkUpdates)
			ThreadCheckVersions.init(e.player, false);
		
		new LMPlayerEvent.LoggedIn(p, e.player, first).post();
		
		{
			NBTTagCompound data = new NBTTagCompound();
			data.setString("UUID", p.uuid.toString());
			LMNetHandler.INSTANCE.sendToAll(new MessageCustomServerAction(ACTION_PLAYER_JOINED, data));
		}
		
		e.player.refreshDisplayName();
	}
	
	@SubscribeEvent
	public void playerLoggedOut(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.player);
		if(p != null) new LMPlayerEvent.LoggedOut(p, e.player).post();
	}
	
	@SubscribeEvent
	public void registerWaila(RegisterWailaEvent e)
	{
		e.register(IWailaTile.Stack.class, new WailaLMTile(e, WailaType.STACK));
		e.register(IWailaTile.Head.class, new WailaLMTile(e, WailaType.HEAD));
		e.register(IWailaTile.Body.class, new WailaLMTile(e, WailaType.BODY));
		e.register(IWailaTile.Tail.class, new WailaLMTile(e, WailaType.TAIL));
		
		if(LCConfig.General.addWailaTanks) e.register(IFluidHandler.class, new WailaTankHandler(e));
	}
	
	@SubscribeEvent
	public void worldLoaded(WorldEvent.Load e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			File f = LatCore.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat"));
			
			LMPlayer.list.clear();
			
			if(f.exists())
			{
				try
				{
					NBTTagCompound tag = NBTHelper.readMap(new FileInputStream(f));
					
					new LoadCustomLMDataEvent(tag).post();
					
					NBTTagList players = tag.getTagList("Players", NBTHelper.MAP);
					
					for(int i = 0; i < players.tagCount(); i++)
					{
						NBTTagCompound tag1 = players.getCompoundTagAt(i);
						LMPlayer p = new LMPlayer(UUID.fromString(tag1.getString("UUID")), tag1.getString("Name"));
						LMPlayer.list.add(p);
						
						new LMPlayerEvent.DataLoaded(p).post();
					}
					
					LMGamerules.readFromNBT(tag, "Gamerules");
					
					LatCoreMC.logger.info("LatCoreMC.dat loaded");
				}
				catch(Exception ex)
				{
					LatCoreMC.logger.warn("Error occured while loading LatCoreMC.dat!");
					ex.printStackTrace();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playerLoaded(PlayerEvent.LoadFromFile e)
	{
		LatCoreMC.logger.info(e.entityPlayer.getCommandSenderName() + ".dat loaded");
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
				
				NBTTagList players = new NBTTagList();
				
				for(int i = 0; i < LMPlayer.list.size(); i++)
				{
					NBTTagCompound tag1 = new NBTTagCompound();
					
					LMPlayer p = LMPlayer.list.get(i);
					p.writeToNBT(tag1);
					
					new LMPlayerEvent.DataSaved(p).post();
					
					tag1.setString("UUID", p.uuid.toString());
					tag1.setString("Name", p.username);
					
					players.appendTag(tag1);
				}
				
				tag.setTag("Players", players);
				
				LMGamerules.writeToNBT(tag, "Gamerules");
				
				new SaveCustomLMDataEvent(tag).post();
				
				NBTHelper.writeMap(new FileOutputStream(f), tag);
			}
			catch(Exception ex)
			{
				LatCoreMC.logger.warn("Error occured while saving LatCoreMC.dat!");
				ex.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void playerLoaded(PlayerEvent.SaveToFile e)
	{
		LatCoreMC.logger.info(e.entityPlayer.getCommandSenderName() + ".dat saved");
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
		if(e.ctrlDown)
		{
			if(e.side.isServer() && e.player instanceof EntityPlayerMP)
				LMNetHandler.INSTANCE.sendTo(new MessageCustomServerAction(LCEventHandler.ACTION_OPEN_FRIENDS_GUI, null), (EntityPlayerMP)e.player);
			e.setCanceled(true);
		}
	}
}