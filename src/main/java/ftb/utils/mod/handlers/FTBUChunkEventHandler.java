package ftb.utils.mod.handlers;

import ftb.lib.EventBusHelper;
import ftb.lib.FTBLib;
import ftb.lib.LMDimUtils;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.FTBUFinals;
import ftb.utils.mod.config.FTBUConfigChunkloading;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ClaimedChunk;
import latmod.lib.LMUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback, ForgeChunkManager.OrderedLoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	private final Map<Integer, Map<UUID, ForgeChunkManager.Ticket>> table = new HashMap<>();
	
	public void init()
	{
		if(!ForgeChunkManager.getConfig().hasCategory(FTBUFinals.MOD_ID))
		{
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumTicketCount", 2000).setMinValue(0);
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumChunksPerTicket", 30000).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}
		
		EventBusHelper.register(this);
		ForgeChunkManager.setForcedChunkLoadingCallback(FTBU.inst, this);
	}
	
	private ForgeChunkManager.Ticket request(World w, LMPlayerServer player)
	{
		if(w == null || player == null) return null;
		
		Map<UUID, ForgeChunkManager.Ticket> map = table.get(w.provider.dimensionId);
		ForgeChunkManager.Ticket t = (map == null) ? null : map.get(player.getProfile().getId());
		
		if(t == null)
		{
			t = ForgeChunkManager.requestTicket(FTBU.inst, w, ForgeChunkManager.Type.NORMAL);
			if(t == null) return null;
			else
			{
				t.getModData().setString("UUID", player.getStringUUID());
				
				if(map == null)
				{
					map = new HashMap<>();
					table.put(w.provider.dimensionId, map);
				}
				
				map.put(player.getProfile().getId(), t);
			}
		}
		
		return t;
	}
	
	@Override
	public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount)
	{
		table.remove(world.provider.dimensionId);
		List<ForgeChunkManager.Ticket> tickets1 = new ArrayList<>();
		if(tickets.isEmpty() || !FTBUConfigChunkloading.enabled.getAsBoolean()) return tickets1;
		Map<UUID, ForgeChunkManager.Ticket> map = new HashMap<>();
		
		for(ForgeChunkManager.Ticket t : tickets)
		{
			UUID id;
			
			if(t.getModData().hasKey("PID"))
			{
				id = LMPlayerServer.tempPlayerIDMap == null ? null : LMPlayerServer.tempPlayerIDMap.get(t.getModData().getInteger("PID"));
			}
			else
			{
				id = LMUtils.fromString(t.getModData().getString("UUID"));
			}
			
			if(id != null)
			{
				map.put(id, t);
				tickets1.add(t);
			}
		}
		
		table.put(world.provider.dimensionId, map);
		return tickets1;
	}
	
	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		for(ForgeChunkManager.Ticket t : tickets)
		{
			UUID playerID = LMUtils.fromString(t.getModData().getString("UUID"));
			
			if(playerID != null)
			{
				List<ClaimedChunk> chunks = LMWorldServer.inst.claimedChunks.getChunks(LMWorldServer.inst.getPlayer(playerID), world.provider.dimensionId);
				
				if(chunks != null && !chunks.isEmpty()) for(ClaimedChunk c : chunks)
				{
					if(c.isChunkloaded)
					{
						ForgeChunkManager.forceChunk(t, c.getPos());
					}
				}
			}
		}
		
		// force chunks //
		markDirty(world);
	}
	
	public void markDirty(World w)
	{
		if(LMWorldServer.inst == null || FTBLib.getServerWorld() == null) return;
		if(w != null) markDirty0(w);
		
		if(!table.isEmpty())
		{
			//To avoid java.util.ConcurrentModificationException
			World[] worlds = table.keySet().toArray(new World[table.size()]);
			for(World w1 : worlds)
				markDirty0(w1);
		}
	}
	
	private void markDirty0(World w)
	{
		/*int total = 0;
		int totalLoaded = 0;
		int markedLoaded = 0;
		int loaded = 0;
		int unloaded = 0;*/
		
		Map<Long, ClaimedChunk> chunksMap = LMWorldServer.inst.claimedChunks.chunks.get(w.provider.dimensionId);
		
		double max = FTBUConfigChunkloading.enabled.getAsBoolean() ? FTBUConfigChunkloading.max_player_offline_hours.getAsDouble() : -2D;
		
		if(chunksMap != null) for(ClaimedChunk c : chunksMap.values())
		{
			//total++;
			
			boolean isLoaded = c.isChunkloaded;
			
			if(c.isChunkloaded)
			{
				LMPlayerServer p = c.getOwnerS();
				if(p == null) isLoaded = false;
				else
				{
					if(max == -2D) isLoaded = false;
					else if(max == -1D) isLoaded = true;
					else if(max == 0D) isLoaded = p.isOnline();
					else if(max > 0D)
					{
						if(!p.isOnline())
						{
							if(max > 0D && p.stats.getLastSeenDeltaInHours(p) > max)
							{
								isLoaded = false;
								//if(c.isForced) unloaded.add(p.getPlayerID());
							}
						}
					}
				}
			}
			
			//if(isLoaded) totalLoaded++;
			//if(c.isChunkloaded) markedLoaded++;
			
			if(c.isForced != isLoaded)
			{
				ForgeChunkManager.Ticket ticket = request(LMDimUtils.getWorld(c.dim), c.getOwnerS());
				
				if(ticket != null)
				{
					if(isLoaded)
					{
						ForgeChunkManager.forceChunk(ticket, c.getPos());
						//loaded++;
					}
					else
					{
						ForgeChunkManager.unforceChunk(ticket, c.getPos());
						//unloaded++;
					}
					
					c.isForced = isLoaded;
				}
			}
		}
		
		//FTBLib.dev_logger.info("Total: " + total + ", Loaded: " + totalLoaded + "/" + markedLoaded + ", DLoaded: " + loaded + ", DUnloaded: " + unloaded);
	}
	
	private void releaseTicket(ForgeChunkManager.Ticket t)
	{
		if(t.getModData().hasKey("UUID"))
		{
			Map<UUID, ForgeChunkManager.Ticket> map = table.get(t.world.provider.dimensionId);
			
			if(map != null)
			{
				map.remove(LMUtils.fromString(t.getModData().getString("UUID")));
				
				if(map.isEmpty())
				{
					table.remove(t.world.provider.dimensionId);
				}
			}
		}
		
		ForgeChunkManager.releaseTicket(t);
	}
	
	public void clear()
	{
		table.clear();
	}
}