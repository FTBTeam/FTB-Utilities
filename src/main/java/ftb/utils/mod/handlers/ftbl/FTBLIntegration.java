package ftb.utils.mod.handlers.ftbl;

import com.google.gson.JsonElement;
import ftb.lib.*;
import ftb.lib.api.*;
import ftb.lib.api.friends.ILMPlayer;
import ftb.lib.api.item.LMInvUtils;
import ftb.lib.mod.FTBUIntegration;
import ftb.utils.api.*;
import ftb.utils.api.guide.ServerGuideFile;
import ftb.utils.badges.ServerBadges;
import ftb.utils.mod.FTBUTicks;
import ftb.utils.mod.config.*;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.net.*;
import ftb.utils.world.*;
import ftb.utils.world.claims.ClaimedChunks;
import ftb.utils.world.ranks.Ranks;
import latmod.lib.*;
import latmod.lib.util.Phase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.*;

import java.io.File;
import java.util.List;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
	private static boolean first_login, send_all;
	
	public void onReloaded(EventFTBReload e)
	{
		FTBUConfigGeneral.onReloaded(e.world.side);
		
		if(e.world.side.isServer())
		{
			if(LMWorldServer.inst == null) return;
			
			for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
				p.refreshStats();
			
			ServerGuideFile.CachedInfo.reload();
			Ranks.reload();
			ServerBadges.reload();
			
			if(FTBLib.getServerWorld() != null) FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
	
	public final void onFTBWorldServer(EventFTBWorldServer e)
	{
		File latmodFolder = new File(FTBLib.folderWorld, "latmod/");
		if(!latmodFolder.exists()) latmodFolder = new File(FTBLib.folderWorld, "LatMod/");
		
		LMWorldServer.inst = new LMWorldServer(latmodFolder);
		
		File file = new File(latmodFolder, "LMWorld.json");
		JsonElement obj = LMJsonUtils.getJsonElement(file);
		if(obj.isJsonObject()) LMWorldServer.inst.load(obj.getAsJsonObject(), Phase.PRE);
		
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
		
		FTBUTicks.serverStarted();
	}
	
	public void onFTBWorldClient(EventFTBWorldClient e)
	{
	}
	
	public final void onFTBWorldServerClosed()
	{
		LMWorldServer.inst.close();
		LMWorldServer.inst = null;
	}
	
	public final void onServerTick(World w)
	{
		if(w.provider.dimensionId == 0)
		{
			FTBUTicks.update();
		}
	}
	
	public final void onPlayerJoined(EntityPlayerMP ep, Phase phase)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		
		if(phase == Phase.PRE)
		{
			first_login = (p == null);
			send_all = false;
			
			if(first_login)
			{
				p = new LMPlayerServer(LMWorldServer.inst, LMPlayerServer.nextPlayerID(), ep.getGameProfile());
				LMWorldServer.inst.playerMap.put(p.playerID, p);
				send_all = true;
			}
			else if(!p.getProfile().getName().equals(ep.getCommandSenderName()))
			{
				p.setProfile(ep.getGameProfile());
				send_all = true;
			}
			
			p.setPlayer(ep);
		}
		else
		{
			p.refreshStats();
			
			new EventLMPlayerServer.LoggedIn(p, ep, first_login).post();
			new MessageLMPlayerLoggedIn(p, first_login, true).sendTo(send_all ? null : ep);
			for(EntityPlayerMP ep1 : FTBLib.getAllOnlinePlayers(ep))
				new MessageLMPlayerLoggedIn(p, first_login, false).sendTo(ep1);
			
			if(first_login)
			{
				List<ItemStack> items = FTBUConfigLogin.getStartingItems(ep.getUniqueID());
				if(items != null && !items.isEmpty()) for(ItemStack is : items)
					LMInvUtils.giveItem(ep, is);
			}
			
			//new MessageLMPlayerInfo(p.playerID).sendTo(null);
			FTBUConfigLogin.printMotd(ep);
			Backups.hadPlayer = true;
			
			p.checkNewFriends();
			new MessageAreaUpdate(p, p.getPos(), 3, 3).sendTo(ep);
			ServerBadges.sendToPlayer(ep);
			
			FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
	
	public final ILMPlayer getLMPlayer(Object player)
	{
		LMWorld w = LMWorld.getWorld();
		return (w == null) ? null : w.getPlayer(player);
	}
	
	public final String[] getPlayerNames(boolean online)
	{ return LMWorldServer.inst.getAllPlayerNames(Boolean.valueOf(online)); }
	
	public final void writeWorldData(ByteIOStream io, EntityPlayerMP ep)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		io.writeInt(p.playerID);
		LMWorldServer.inst.writeDataToNet(io, p, true);
	}
	
	public void readWorldData(ByteIOStream io)
	{
		
	}
	
	public boolean hasClientWorld()
	{ return false; }
	
	public void renderWorld(float pt)
	{ }
	
	public void onTooltip(ItemTooltipEvent e)
	{
	}
	
	public void onRightClick(PlayerInteractEvent e)
	{
		if(e.entityPlayer instanceof FakePlayer || e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
		else if(!ClaimedChunks.canPlayerInteract(e.entityPlayer, new ChunkCoordinates(e.x, e.y, e.z), e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
			e.setCanceled(true);
	}
}