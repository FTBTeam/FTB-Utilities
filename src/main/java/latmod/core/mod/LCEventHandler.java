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
import cpw.mods.fml.relauncher.Side;

public class LCEventHandler
{
	public static final String ACTION_OPEN_FRIENDS_GUI = "OpenFriendsGUI";
	
	public static final LCEventHandler instance = new LCEventHandler();
	
	public static final UUID UUID_LatvianModder  = UUID.fromString("5afb9a5b-207d-480e-8879-67bc848f9a8f");
	
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
			p = new LMPlayer(e.player.getUniqueID(), e.player.getCommandSenderName());
			
			if(p.uuid.equals(UUID_LatvianModder))
				p.setCustomName("LatvianModder");
			
			LMPlayer.list.add(p);
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
		
		if(p != null && p.isOnline())
		{
			p.setOnline(false);
			p.sendUpdate(LMPlayer.ACTION_LOGGED_OUT);
			new LMPlayerEvent.LoggedOut(p, Side.SERVER, e.player).post();
		}
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
			File f = new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat");
			
			if(f.exists())
			{
				try
				{
					NBTTagCompound tag = NBTHelper.readMap(new FileInputStream(f));
					loadAllData(tag);
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
		LMPlayer.list.clear();
		new LoadCustomLMDataEvent(EventLM.Phase.PRE, tag).post();
		
		FastMap<UUID, NBTTagCompound> playerData = new FastMap<UUID, NBTTagCompound>();
		
		if(tag.func_150299_b("Players") == NBTHelper.LIST)
		{
			NBTTagList players = tag.getTagList("Players", NBTHelper.MAP);
			
			for(int i = 0; i < players.tagCount(); i++)
			{
				NBTTagCompound tag1 = players.getCompoundTagAt(i);
				LMPlayer p = new LMPlayer(UUID.fromString(tag1.getString("UUID")), tag1.getString("Name"));
				LMPlayer.list.add(p);
				playerData.put(p.uuid, tag1);
			}
			
			LatCoreMC.logger.info("Found old LMPlayers");
		}
		else
		{
			FastMap<String, NBTTagCompound> map = NBTHelper.toFastMapWithType(tag.getCompoundTag("Players"));
			
			for(int i = 0; i < map.size(); i++)
			{
				NBTTagCompound tag1 = map.values.get(i);
				LMPlayer p = new LMPlayer(UUID.fromString(tag1.getString("UUID")), map.keys.get(i));
				LMPlayer.list.add(p);
				playerData.put(p.uuid, tag1);
			}
		}
		
		for(int i = 0; i < LMPlayer.list.size(); i++)
		{
			LMPlayer p = LMPlayer.list.get(i);
			p.readFromNBT(playerData.get(p.uuid));
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
		
		for(int i = 0; i < LMPlayer.list.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayer p = LMPlayer.list.get(i);
			p.writeToNBT(tag1);
			new LMPlayerEvent.DataSaved(p).post();
			tag1.setString("UUID", p.uuid.toString());
			
			players.setTag(p.username, tag1);
		}
		
		tag.setTag("Players", players);
		
		LMGamerules.writeToNBT(tag);
		new SaveCustomLMDataEvent(tag).post();
	}
	
	public void updateAllData(EntityPlayerMP ep)
	{
		if(ep != null) LMNetHandler.INSTANCE.sendTo(new MessageUpdateLMData(), ep);
		else LMNetHandler.INSTANCE.sendToAll(new MessageUpdateLMData());
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
			LMNetHandler.INSTANCE.sendTo(new MessageCustomServerAction(LCEventHandler.ACTION_OPEN_FRIENDS_GUI, null), (EntityPlayerMP)e.player);
			e.setCanceled(true);
		}
	}
}