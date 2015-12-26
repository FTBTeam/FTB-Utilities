package latmod.ftbu.mod.handlers;

import ftb.lib.LMDimUtils;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.ftbu.world.*;
import latmod.ftbu.world.claims.*;
import latmod.lib.*;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.List;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	public final FastMap<Integer, FastMap<Integer, ForgeChunkManager.Ticket>> ticketMap = new FastMap<>();
	
	public ForgeChunkManager.Ticket request(World w, LMPlayerServer player)
	{
		if(w == null || player == null) return null;

		FastMap<Integer, ForgeChunkManager.Ticket> map = ticketMap.get(Integer.valueOf(w.provider.dimensionId));
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
					map = new FastMap<>();
					ticketMap.put(Integer.valueOf(w.provider.dimensionId), map);
				}

				map.put(player.playerID, t);
			}
		}
		
		return t;
	}
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> list, World world)
	{
		Integer dim = Integer.valueOf(world.provider.dimensionId);
		ticketMap.remove(dim);
		FastMap<Integer, ForgeChunkManager.Ticket> newMap = new FastMap<>();

		for(ForgeChunkManager.Ticket t : list)
		{
			int playerID = t.getModData().getInteger("PlayerID");

			if(playerID == 0) ForgeChunkManager.releaseTicket(t);
			else newMap.put(Integer.valueOf(playerID), t);
		}

		ticketMap.put(dim, newMap);
		markDirty(dim);
	}

	public void worldLoadEvent(World w)
	{
		if(w != null && !w.isRemote && LMWorldServer.inst != null)
			markDirty(Integer.valueOf(w.provider.dimensionId));
	}

	public void markDirty(Integer dim)
	{
		if(LMWorldServer.inst == null) return;

		/*
		int total = 0;
		int totalLoaded = 0;
		int markedLoaded = 0;
		int loaded = 0;
		int unloaded = 0;
		*/

		ChunkloaderType type = FTBUConfigGeneral.chunkloader_type.get();

		Iterable<ClaimedChunk> allChunks = (dim == null) ? LMWorldServer.inst.claimedChunks.getAllChunks() : LMWorldServer.inst.claimedChunks.chunks.get(dim);

		for(ClaimedChunk c : allChunks)
		{
			//total++;

			boolean isLoaded = c.isChunkloaded;

			if(type == ChunkloaderType.DISABLED)
				isLoaded = false;
			else if(c.getOwner() == null)
				isLoaded = false;
			else if(type == ChunkloaderType.PLAYER)
				isLoaded = c.getOwner().isOnline();

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

		FastList<ForgeChunkManager.Ticket> releasedTickets = new FastList<>();
		FastMap<Integer, ForgeChunkManager.Ticket> map = ticketMap.get(dim);

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
	{ return request(LMDimUtils.getWorld(c.dim), c.getOwner().toPlayerMP()); }
}