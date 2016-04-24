package ftb.utils.mod.handlers.ftbl;

import com.google.gson.JsonElement;
import ftb.lib.FTBLib;
import ftb.lib.LMNBTUtils;
import ftb.lib.api.EventFTBReload;
import ftb.lib.api.EventFTBWorldClient;
import ftb.lib.api.EventFTBWorldServer;
import ftb.lib.api.friends.ILMPlayer;
import ftb.lib.mod.FTBUIntegration;
import ftb.utils.api.EventLMWorldServer;
import ftb.utils.api.guide.ServerInfoFile;
import ftb.utils.badges.ServerBadges;
import ftb.utils.mod.FTBUTicks;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorld;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ClaimedChunks;
import ftb.utils.world.ranks.Ranks;
import latmod.lib.LMJsonUtils;
import latmod.lib.util.Phase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.io.File;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
	@Override
	public void onReloaded(EventFTBReload e)
	{
		if(e.world.side.isServer())
		{
			if(LMWorldServer.inst == null) return;
			
			for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
				p.refreshStats();
			
			ServerInfoFile.CachedInfo.reload();
			Ranks.reload();
			ServerBadges.reload();
			
			FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
	
	@Override
	public final void onFTBWorldServer(EventFTBWorldServer e)
	{
		File latmodFolder = new File(FTBLib.folderWorld, "LatMod/");
		
		LMWorldServer.inst = new LMWorldServer(latmodFolder);
		
		File file = new File(latmodFolder, "LMWorld.json");
		JsonElement obj = LMJsonUtils.fromJson(file);
		if(obj.isJsonObject()) LMWorldServer.inst.load(obj.getAsJsonObject(), Phase.PRE);
		
		new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.PRE).post();
		
		NBTTagCompound tagPlayers = LMNBTUtils.readMap(new File(latmodFolder, "LMPlayers.dat"));
		if(tagPlayers != null && tagPlayers.hasKey("Players"))
		{
			LMWorldServer.inst.readPlayersFromServer(tagPlayers.getCompoundTag("Players"));
		}
		
		for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
			p.setPlayer(null);
		
		if(obj.isJsonObject()) LMWorldServer.inst.load(obj.getAsJsonObject(), Phase.POST);
		
		file = new File(latmodFolder, "ClaimedChunks.json");
		
		if(file.exists())
		{
			obj = LMJsonUtils.fromJson(file);
			if(obj.isJsonObject()) LMWorldServer.inst.claimedChunks.load(obj.getAsJsonObject());
		}
		
		new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.POST).post();
		
		FTBUTicks.serverStarted();
	}
	
	@Override
	public void onFTBWorldClient(EventFTBWorldClient e)
	{
	}
	
	@Override
	public final void onFTBWorldServerClosed()
	{
		LMWorldServer.inst.close();
		LMWorldServer.inst = null;
	}
	
	@Override
	public final void onServerTick(World w)
	{
		if(w.provider.dimensionId == 0)
		{
			FTBUTicks.update();
		}
	}
	
	@Override
	public final ILMPlayer getLMPlayer(Object player)
	{
		LMWorld w = LMWorld.getWorld();
		return (w == null) ? null : w.getPlayer(player);
	}
	
	@Override
	public final String[] getPlayerNames(boolean online)
	{ return LMWorldServer.inst.getAllPlayerNames(Boolean.valueOf(online)); }
	
	@Override
	public boolean hasClientWorld()
	{ return false; }
	
	@Override
	public void renderWorld(float pt)
	{ }
	
	@Override
	public void onTooltip(ItemTooltipEvent e)
	{
	}
	
	@Override
	public void onRightClick(PlayerInteractEvent e)
	{
		if(e.entityPlayer instanceof FakePlayer || e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
		else if(!ClaimedChunks.canPlayerInteract(e.entityPlayer, new ChunkCoordinates(e.x, e.y, e.z), e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
			e.setCanceled(true);
	}
}