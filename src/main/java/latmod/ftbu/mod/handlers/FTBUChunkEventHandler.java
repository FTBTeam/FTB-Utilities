package latmod.ftbu.mod.handlers;

import ftb.lib.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.world.*;
import latmod.ftbu.world.claims.*;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.*;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	public final HashMap<Integer, HashMap<Integer, ForgeChunkManager.Ticket>> ticketMap = new HashMap<>();

	public void refreshMaxChunksCount()
	{
		if(!ForgeChunkManager.getConfig().hasCategory(FTBUFinals.MOD_ID))
		{
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumTicketCount", 2000).setMinValue(0);
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumChunksPerTicket", 30000).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}
	}

	public ForgeChunkManager.Ticket request(World w, LMPlayerServer player)
	{
		if(w == null || player == null) return null;

		HashMap<Integer, ForgeChunkManager.Ticket> map = ticketMap.get(Integer.valueOf(w.provider.dimensionId));
		ForgeChunkManager.Ticket t = (map == null) ? null : map.get(player.playerID);
		
		if(t == null)
		{
			t = ForgeChunkManager.requestTicket(FTBU.inst, w, ForgeChunkManager.Type.NORMAL);
			if(t == null) return null;
			else
			{
				t.getModData().setInteger("PlayerID", player.playerID);

				if(map == null)
				{
					map = new HashMap<>();
					ticketMap.put(Integer.valueOf(w.provider.dimensionId), map);
				}

				map.put(player.playerID, t);
			}
		}
		
		return t;
	}
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> list, World world)
	{
		FTBLib.dev_logger.info(LMWorldServer.inst);
		Integer dim = Integer.valueOf(world.provider.dimensionId);
		ticketMap.remove(dim);
		HashMap<Integer, ForgeChunkManager.Ticket> newMap = new HashMap<>();

		for(ForgeChunkManager.Ticket t : list)
		{
			int playerID = t.getModData().getInteger("PlayerID");

			if(playerID == 0) ForgeChunkManager.releaseTicket(t);
			else newMap.put(Integer.valueOf(playerID), t);
		}

		ticketMap.put(dim, newMap);
		markDirty(dim);
	}

	public void markDirty(Integer dim)
	{
		if(LMWorldServer.inst == null) return;

		if(dim == null)
		{
			for(Integer dim1 : ticketMap.keySet())
				markDirty(dim1);
			return;
		}

		/*int total = 0;
		int totalLoaded = 0;
		int markedLoaded = 0;
		int loaded = 0;
		int unloaded = 0;*/

		Map<Long, ClaimedChunk> chunksMap = LMWorldServer.inst.claimedChunks.chunks.get(dim);

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
								if(c.isForced)
									FTBLib.logger.info("Unloading " + p.getName() + " chunks for being offline for too long");
							}
						}
					}
				}
			}

			//if(isLoaded) totalLoaded++;
			//if(c.isChunkloaded) markedLoaded++;

			if(c.isForced != isLoaded)
			{
				ForgeChunkManager.Ticket ticket = getTicket(c);

				if(ticket != null)
				{
					if(isLoaded)
					{
						ForgeChunkManager.forceChunk(ticket, c);
						//loaded++;
					}
					else
					{
						ForgeChunkManager.unforceChunk(ticket, c);
						//unloaded++;
					}

					c.isForced = isLoaded;
				}
			}
		}

		//FTBLib.dev_logger.info("Total: " + total + ", Loaded: " + totalLoaded + "/" + markedLoaded + ", DLoaded: " + loaded + ", DUnloaded: " + unloaded);
		cleanupTickets(dim);
	}

	private void cleanupTickets(Integer dim)
	{
		if(dim == null)
		{
			for(Integer dim1 : ticketMap.keySet())
				cleanupTickets(dim1);
			return;
		}

		ArrayList<ForgeChunkManager.Ticket> releasedTickets = new ArrayList<>();
		HashMap<Integer, ForgeChunkManager.Ticket> map = ticketMap.get(dim);

		if(map == null) return;

		for(ForgeChunkManager.Ticket t : map.values())
		{
			for(ChunkCoordIntPair c : t.getChunkList())
			{
				ClaimedChunk cl = LMWorldServer.inst.claimedChunks.getChunk(dim.intValue(), c.chunkXPos, c.chunkZPos);
				if(cl == null) ForgeChunkManager.unforceChunk(t, c);
			}

			if(t.getChunkList().isEmpty()) releasedTickets.add(t);
		}

		if(!releasedTickets.isEmpty())
		{
			for(ForgeChunkManager.Ticket t : releasedTickets)
			{
				map.remove(t.getModData().getInteger("PlayerID"));
				ForgeChunkManager.releaseTicket(t);
			}

			if(map.isEmpty()) ticketMap.remove(dim);

			//FTBLib.dev_logger.info("Released " + releasedTickets.size() + " tickets");
		}
	}

	public ForgeChunkManager.Ticket getTicket(ClaimedChunk c)
	{
		if(c == null) return null;
		return request(LMDimUtils.getWorld(c.dim), c.getOwnerS());
	}
}