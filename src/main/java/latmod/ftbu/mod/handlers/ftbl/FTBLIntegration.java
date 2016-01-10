package latmod.ftbu.mod.handlers.ftbl;

import com.google.gson.*;
import ftb.lib.*;
import ftb.lib.api.*;
import ftb.lib.item.LMInvUtils;
import ftb.lib.mod.FTBUIntegration;
import latmod.ftbu.api.*;
import latmod.ftbu.api.guide.ServerGuideFile;
import latmod.ftbu.badges.ServerBadges;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.config.*;
import latmod.ftbu.mod.handlers.FTBUChunkEventHandler;
import latmod.ftbu.net.*;
import latmod.ftbu.world.*;
import latmod.ftbu.world.ranks.Ranks;
import latmod.lib.*;
import latmod.lib.util.Phase;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.File;
import java.util.List;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
	public long nextChunkloaderUpdate = 0L;
	
	public void onReloaded(EventFTBReload e)
	{
		FTBUConfigGeneral.onReloaded(e.world.side);
		
		if(e.world.side.isServer())
		{
			if(LMWorldServer.inst == null) return;
			
			if(FTBUConfigGeneral.restart_timer.get() > 0) FTBUTicks.serverStarted();
			
			for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
				p.refreshStats();
			
			ServerGuideFile.CachedInfo.reload();
			Ranks.reload();
			ServerBadges.reload();
			
			if(FTBLib.getServerWorld() != null)
			{
				FTBUChunkEventHandler.instance.markDirty(null);
				ServerBadges.sendToPlayer(null);
			}
		}
		else FTBU.proxy_ftbl_int.onReloadedClient(e);
	}
	
	public void onFTBWorldServer(EventFTBWorldServer e)
	{
		File latmodFolder = new File(FTBLib.folderWorld, "LatMod/");
		File file = new File(latmodFolder, "LMWorld.dat");
		
		LMWorldServer.inst = new LMWorldServer(latmodFolder);
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
		
		for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
			p.setPlayer(null);
		
		if(obj.isJsonObject()) LMWorldServer.inst.load(obj.getAsJsonObject(), Phase.POST);
		
		file = new File(latmodFolder, "ClaimedChunks.json");
		
		if(file.exists())
		{
			obj = LMJsonUtils.getJsonElement(file);
			if(obj.isJsonObject()) LMWorldServer.inst.claimedChunks.load(obj.getAsJsonObject());
		}
		
		new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.POST).post();
		
		nextChunkloaderUpdate = LMUtils.millis() + 10000L;
	}
	
	public void onFTBWorldClient(EventFTBWorldClient e)
	{
		FTBU.proxy_ftbl_int.onFTBWorldClient(e);
	}
	
	public void onFTBWorldServerClosed()
	{
		LMWorldServer.inst.close();
		LMWorldServer.inst = null;
	}
	
	public void onServerTick(World w)
	{
		if(w.provider.dimensionId == 0)
		{
			FTBUTicks.update();
			
			long now = LMUtils.millis();
			if(nextChunkloaderUpdate < now)
			{
				nextChunkloaderUpdate = now + 300000L;
				FTBUChunkEventHandler.instance.markDirty(null);
			}
			
			if(Backups.shouldKillThread)
			{
				Backups.shouldKillThread = false;
				boolean wasBackup = Backups.thread instanceof ThreadBackup;
				Backups.thread = null;
				
				if(wasBackup)
				{
					try
					{
						new CommandSaveOn().processCommand(FTBLib.getServer(), new String[0]);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	public void onPlayerJoined(EntityPlayerMP ep)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		
		boolean first = (p == null);
		boolean sendAll = false;
		
		if(first)
		{
			p = new LMPlayerServer(LMWorldServer.inst, LMPlayerServer.nextPlayerID(), ep.getGameProfile());
			LMWorldServer.inst.playerMap.put(p.playerID, p);
			sendAll = true;
		}
		else if(!p.getName().equals(p.gameProfile.getName()))
		{
			p.gameProfile = ep.getGameProfile();
			sendAll = true;
		}
		
		p.setPlayer(ep);
		p.refreshStats();
		
		new EventLMPlayerServer.LoggedIn(p, ep, first).post();
		new MessageLMPlayerLoggedIn(p, first, true).sendTo(sendAll ? null : ep);
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
		ServerBadges.sendToPlayer(ep);
		
		FTBUChunkEventHandler.instance.markDirty(null);
	}
	
	public int getPlayerID(Object player)
	{ return LMWorld.getWorld().getPlayerID(player); }
	
	public String[] getPlayerNames(boolean online)
	{ return LMWorldServer.inst.getAllPlayerNames(Boolean.valueOf(online)); }
	
	public void writeWorldData(ByteIOStream io, EntityPlayerMP ep)
	{
		int id = getPlayerID(ep);
		io.writeInt(id);
		LMWorldServer.inst.writeDataToNet(io, id);
	}
	
	public void readWorldData(ByteIOStream io)
	{ FTBU.proxy_ftbl_int.readWorldData(io); }
}