package latmod.ftbu.mod;

import java.io.File;
import java.util.List;

import ftb.lib.*;
import ftb.lib.api.*;
import ftb.lib.api.config.ConfigListRegistry;
import ftb.lib.item.LMInvUtils;
import ftb.lib.mod.FTBUIntegration;
import latmod.ftbu.api.*;
import latmod.ftbu.mod.config.*;
import latmod.ftbu.mod.handlers.FTBUChunkEventHandler;
import latmod.ftbu.net.*;
import latmod.ftbu.world.*;
import latmod.lib.Phase;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;

public class FTBLIntegration extends FTBUIntegration // FTBLIntegrationClient
{
	public void onReloaded(EventFTBReload e)
	{
		if(e.side.isClient() || LMWorldServer.inst == null) return;
		
		if(FTBUConfigGeneral.restartTimer.get() > 0)
			FTBUTicks.serverStarted();
		
		for(LMPlayer p : LMWorldServer.inst.players)
			p.toPlayerMP().refreshStats();
	}
	
	public void onModeSet(EventFTBModeSet e)
	{
		FTBUConfigGeneral.onReloaded(e.side);
	}
	
	public void onFTBWorldServer(EventFTBWorldServer e)
	{
		if(FTBLib.isServer() && e.worldMC.provider.dimensionId == 0 && e.worldMC instanceof WorldServer)
		{
			ConfigListRegistry.reloadInstance();
			
			File latmodFolder = new File(e.worldMC.getSaveHandler().getWorldDirectory(), "latmod/");
			NBTTagCompound tagWorldData = LMNBTUtils.readMap(new File(latmodFolder, "LMWorld.dat"));
			if(tagWorldData == null) tagWorldData = new NBTTagCompound();
			LMWorldServer.inst = new LMWorldServer((WorldServer)e.worldMC, latmodFolder);
			LMWorldServer.inst.load(tagWorldData);
			
			new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.PRE).post();
			
			NBTTagCompound tagPlayers = LMNBTUtils.readMap(new File(latmodFolder, "LMPlayers.dat"));
			if(tagPlayers != null && tagPlayers.hasKey("Players"))
			{
				LMPlayerServer.lastPlayerID = tagPlayers.getInteger("LastID");
				LMWorldServer.inst.readPlayersFromServer(tagPlayers.getCompoundTag("Players"));
			}
			
			for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
				LMWorldServer.inst.players.get(i).toPlayerMP().setPlayer(null);
			
			FTBUChunkEventHandler.worldLoadEvent(e.worldMC);
			
			new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.POST).post();
		}
	}
	
	public void onFTBWorldClient(EventFTBWorldClient e)
	{
	}
	
	public void onServerTick(World w)
	{
		if(w.provider.dimensionId == 0) FTBUTicks.update();
	}
	
	public void onPlayerJoined(EntityPlayer player)
	{
		if(!(player instanceof EntityPlayerMP)) return;
		EntityPlayerMP ep = (EntityPlayerMP)player;
		
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		
		boolean first = (p == null);
		boolean sendAll = false;
		
		if(first)
		{
			p = new LMPlayerServer(LMWorldServer.inst, LMPlayerServer.nextPlayerID(), ep.getGameProfile());
			LMWorldServer.inst.players.add(p);
			sendAll = true;
		}
		else if(!p.getName().equals(p.gameProfile.getName()))
		{
			p.setName(p.gameProfile.getName());
			sendAll = true;
		}
		
		p.setPlayer(ep);
		p.refreshStats();
		
		new MessageLMWorldJoined(p.playerID).sendTo(sendAll ? null : ep);
		new EventLMPlayerServer.LoggedIn(p, ep, first).post();
		new MessageLMPlayerLoggedIn(p, first, true).sendTo(ep);
		for(EntityPlayerMP ep1 : FTBLib.getAllOnlinePlayers(ep))
			new MessageLMPlayerLoggedIn(p, first, false).sendTo(ep1);
		
		if(first)
		{
			List<ItemStack> items = FTBUConfigLogin.getStartingItems(ep.getUniqueID());
			if(items != null && !items.isEmpty()) for(ItemStack is : items)
				LMInvUtils.giveItem(ep, is);
		}
		
		//new MessageLMPlayerInfo(p.playerID).sendTo(null);
		FTBUConfigLogin.printMotd(ep);
		Backups.shouldRun = true;
		
		//if(first) teleportToSpawn(ep);
		p.checkNewFriends();
		new MessageAreaUpdate(p.getPos(), 3, 3).sendTo(ep);
	}
	
	public int getPlayerID(Object player)
	{ return LMWorld.getWorld().getPlayerID(player); }
}