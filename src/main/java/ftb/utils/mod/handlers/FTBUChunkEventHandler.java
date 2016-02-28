package ftb.utils.mod.handlers;

import com.google.common.collect.MapMaker;
import ftb.lib.*;
import ftb.utils.mod.*;
import ftb.utils.mod.config.FTBUConfigGeneral;
import ftb.utils.world.*;
import ftb.utils.world.claims.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.*;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback, ForgeChunkManager.OrderedLoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	private final Map<World, Map<Integer, ForgeChunkManager.Ticket>> table = new MapMaker().weakKeys().makeMap();
	private static final String PLAYER_ID_TAG = "PID";
	
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
		
		Integer playerID = Integer.valueOf(player.getPlayerID());
		
		Map<Integer, ForgeChunkManager.Ticket> map = table.get(w);
		ForgeChunkManager.Ticket t = (map == null) ? null : map.get(playerID);
		
		if(t == null)
		{
			t = ForgeChunkManager.requestTicket(FTBU.inst, w, ForgeChunkManager.Type.NORMAL);
			if(t == null) return null;
			else
			{
				t.getModData().setInteger(PLAYER_ID_TAG, playerID);
				
				if(map == null)
				{
					map = new HashMap<>();
					table.put(w, map);
				}
				
				map.put(playerID, t);
			}
		}
		
		return t;
	}
	
	public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount)
	{
		table.remove(world);
		List<ForgeChunkManager.Ticket> tickets1 = new ArrayList<>();
		if(tickets.isEmpty() || FTBUConfigGeneral.disable_chunkloading.get()) return tickets1;
		Map<Integer, ForgeChunkManager.Ticket> map = new HashMap<>();
		
		for(ForgeChunkManager.Ticket t : tickets)
		{
			int playerID = t.getModData().getInteger(PLAYER_ID_TAG);
			
			if(playerID > 0)
			{
				map.put(playerID, t);
				tickets1.add(t);
			}
		}
		
		table.put(world, map);
		return tickets1;
	}
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		for(ForgeChunkManager.Ticket t : tickets)
		{
			int playerID = t.getModData().getInteger(PLAYER_ID_TAG);
			
			if(playerID > 0)
			{
				
				List<ClaimedChunk> chunks = LMWorldServer.inst.claimedChunks.getChunks(LMWorldServer.inst.getPlayer(playerID), world.provider.dimensionId);
				
				if(chunks != null) for(ClaimedChunk c : chunks)
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
					ChunkloaderType type = p.getRank().config.chunkloader_type.get();
					
					if(type == ChunkloaderType.DISABLED) isLoaded = false;
					else if(type == ChunkloaderType.ONLINE) isLoaded = p.isOnline();
					else if(type == ChunkloaderType.OFFLINE)
					{
						if(!p.isOnline())
						{
							double max = p.getRank().config.offline_chunkloader_timer.get();
							
							if(max > 0D && p.stats.getLastSeenDeltaInHours() > max)
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
		if(t.getModData().hasKey(PLAYER_ID_TAG))
		{
			Map<Integer, ForgeChunkManager.Ticket> map = table.get(t.world);
			
			if(map != null)
			{
				map.remove(t.getModData().getInteger(PLAYER_ID_TAG));
				
				if(map.isEmpty())
				{
					table.remove(t.world);
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