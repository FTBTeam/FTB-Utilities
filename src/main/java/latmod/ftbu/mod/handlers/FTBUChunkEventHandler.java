package latmod.ftbu.mod.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.*;
import latmod.ftbu.world.claims.*;
import latmod.lib.*;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.world.WorldEvent;

import java.util.List;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	public final FastMap<Long, ForgeChunkManager.Ticket> tickets = new FastMap<Long, ForgeChunkManager.Ticket>();
	
	public ForgeChunkManager.Ticket request(World w, LMPlayerServer player)
	{
		Long l = Long.valueOf(Bits.intsToLong(w.provider.dimensionId, player.playerID));
		ForgeChunkManager.Ticket t = tickets.get(l);
		
		if(t == null)
		{
			t = ForgeChunkManager.requestTicket(FTBU.inst, w, ForgeChunkManager.Type.NORMAL);
			if(t == null) return null; else
			{
				t.getModData().setInteger("PlayerID", player.playerID);
				tickets.put(l, t);
			}
		}
		
		return t;
	}
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> list, World world)
	{
		for(ForgeChunkManager.Ticket t : list)
		{
			int playerID = t.getModData().getInteger("PlayerID");

			if(playerID == 0) ForgeChunkManager.releaseTicket(t);
			else
			{
				tickets.put(Long.valueOf(Bits.intsToLong(world.provider.dimensionId, playerID)), t);
				FTBLib.dev_logger.info("Added existing ticket for " + playerID);
			}
		}
	}
	
	@SubscribeEvent
	public void onChunkForced(ForgeChunkManager.ForceChunkEvent e)
	{
		FTBLib.dev_logger.info("Chunk from " + e.ticket.getModId() +  " forced " + e.location);
	}
	
	@SubscribeEvent
	public void onChunkUnforced(ForgeChunkManager.UnforceChunkEvent e)
	{
		FTBLib.dev_logger.info("Chunk from " + e.ticket.getModId() + " unforced " + e.location);
	}
	
	@SubscribeEvent
	public void worldUnloadEvent(WorldEvent.Unload e)
	{
		if(!e.world.isRemote && LMWorldServer.inst != null)
		{
			//for(LMPlayer p : LMWorldServer.inst.players)
			//	p.toPlayerMP().unloadAllChunks(e.world);
		}
	}
	
	public void worldLoadEvent(World w)
	{
		if(w != null && !w.isRemote && LMWorldServer.inst != null)
		{
			for(ClaimedChunk c : LMWorldServer.inst.claimedChunks.getAllChunks())
			{
				if(c.isChunkloaded && c.isForced)
				{
					c.isForced = false;
					ForgeChunkManager.Ticket ticket = getTicket(c);
					if(ticket != null) ForgeChunkManager.unforceChunk(ticket, c.pos);
				}
			}
		}
	}

	public void markDirty()
	{
		System.out.println(LMWorldServer.inst.claimedChunks.getAllChunks().size());
		for(ClaimedChunk c : LMWorldServer.inst.claimedChunks.getAllChunks())
		{
			if(c.isForced != c.isChunkloaded)
			{
				ForgeChunkManager.Ticket ticket = getTicket(c);

				if(ticket != null)
				{
					if(c.isChunkloaded)
					{
						FTBLib.dev_logger.info("Chunk @ " + c.toString() + " loaded");
						ForgeChunkManager.unforceChunk(ticket, c.pos);
					}
					else
					{
						FTBLib.dev_logger.info("Chunk @ " + c.toString() + " unloaded");
						ForgeChunkManager.forceChunk(ticket, c.pos);
					}

					c.isForced = c.isChunkloaded;
					LMPlayer p = c.getOwner();
				}
			}
		}
	}

	public ForgeChunkManager.Ticket getTicket(ClaimedChunk c)
	{
		World w = LMDimUtils.getWorld(c.dim);

		if(w instanceof WorldServer)
		{
			LMPlayer o = c.getOwner();
			if(o != null) return request((WorldServer)w, o.toPlayerMP());
		}

		return null;
	}
}