package latmod.ftbu.mod.handlers.ftbl;

import com.google.gson.*;
import ftb.lib.*;
import ftb.lib.api.*;
import ftb.lib.api.config.ConfigRegistry;
import ftb.lib.item.LMInvUtils;
import ftb.lib.mod.FTBUIntegration;
import latmod.ftbu.api.*;
import latmod.ftbu.api.guide.ServerGuideFile;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.config.*;
import latmod.ftbu.mod.handlers.FTBUChunkEventHandler;
import latmod.ftbu.net.*;
import latmod.ftbu.world.*;
import latmod.lib.*;
import latmod.lib.util.Phase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;

import java.io.File;
import java.util.List;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
	public void onReloaded(EventFTBReload e)
	{
		if(e.side.isServer())
		{
			if(LMWorldServer.inst == null) return;
			
			if(FTBUConfigGeneral.restart_timer.get() > 0)
				FTBUTicks.serverStarted();
			
			for(LMPlayer p : LMWorldServer.inst.players)
				p.toPlayerMP().refreshStats();
			
			ServerGuideFile.CachedInfo.reload();
			FTBUChunkEventHandler.instance.markDirty(null);
		}
		else FTBU.proxy_ftbl_int.onReloadedClient(e);
	}
	
	public void onModeSet(EventFTBModeSet e)
	{
		FTBUConfigGeneral.onReloaded(e.side);
		if(e.side.isClient()) FTBU.proxy_ftbl_int.onModeSetClient(e);
	}
	
	public void onFTBWorldServer(EventFTBWorldServer e)
	{
		if(FTBLib.isServer() && e.worldMC.provider.dimensionId == 0 && e.worldMC instanceof WorldServer)
		{
			ConfigRegistry.reload();
			
			File latmodFolder = new File(e.worldMC.getSaveHandler().getWorldDirectory(), "latmod/");
			File file = new File(latmodFolder, "LMWorld.dat");
			
			LMWorldServer.inst = new LMWorldServer((WorldServer)e.worldMC, latmodFolder);
			JsonElement obj = JsonNull.INSTANCE;
			
			if(file.exists())
			{
				NBTTagCompound tagWorldData = LMNBTUtils.readMap(file);
				if(tagWorldData != null) LMWorldServer.inst.load(tagWorldData);
				LMFileUtils.delete(file);
			}
			else
			{
				file = new File(latmodFolder, "LMWorld.json");
				obj = LMJsonUtils.getJsonElement(file);
				if(obj.isJsonObject()) LMWorldServer.inst.load(obj.getAsJsonObject(), Phase.PRE);
			}
			
			new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.PRE).post();
			
			NBTTagCompound tagPlayers = LMNBTUtils.readMap(new File(latmodFolder, "LMPlayers.dat"));
			if(tagPlayers != null && tagPlayers.hasKey("Players"))
			{
				LMPlayerServer.lastPlayerID = tagPlayers.getInteger("LastID");
				LMWorldServer.inst.readPlayersFromServer(tagPlayers.getCompoundTag("Players"));
			}
			
			for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
				LMWorldServer.inst.players.get(i).toPlayerMP().setPlayer(null);
			
			if(obj.isJsonObject()) LMWorldServer.inst.load(obj.getAsJsonObject(), Phase.POST);
			
			file = new File(latmodFolder, "ClaimedChunks.json");
			obj = JsonNull.INSTANCE;
			
			if(file.exists())
			{
				obj = LMJsonUtils.getJsonElement(file);
				if(obj.isJsonObject()) LMWorldServer.inst.claimedChunks.load(obj.getAsJsonObject());
			}
			
			FTBUChunkEventHandler.instance.worldLoadEvent(e.worldMC);
			
			new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.POST).post();
		}
	}
	
	public void onFTBWorldClient(EventFTBWorldClient e)
	{
		FTBU.proxy_ftbl_int.onFTBWorldClient(e);
	}
	
	public void onServerTick(World w)
	{
		if(w.provider.dimensionId == 0) FTBUTicks.update();
	}
	
	public void onPlayerJoined(EntityPlayerMP ep)
	{
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
			p.gameProfile = ep.getGameProfile();
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
		new MessageAreaUpdate(p, p.getPos(), 3, 3).sendTo(ep);
	}
	
	public int getPlayerID(Object player)
	{ return LMWorld.getWorld().getPlayerID(player); }
	
	public String[] getPlayerNames(boolean online)
	{ return LMWorld.getWorld().getAllPlayerNames(Boolean.valueOf(online)); }
	
	public String[] getOfflinePlayerNames()
	{ return LMWorld.getWorld().players.toStringArray(); }
}