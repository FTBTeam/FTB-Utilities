package latmod.ftbu.mod.handlers;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import latmod.ftbu.world.*;
import latmod.lib.FastMap;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.world.WorldEvent;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	public final FastMap<Long, ForgeChunkManager.Ticket> tickets = new FastMap<Long, ForgeChunkManager.Ticket>();
	
	public ForgeChunkManager.Ticket request(World w, LMPlayerServer player)
	{
		/*
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
		*/
		
		return null;
	}
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		/*
		FastList<String> ticketNames = new FastList<String>();
		
		for(ForgeChunkManager.Ticket t : tickets)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(t.getChunkList());
			ticketNames.add(sb.toString());
			
			ForgeChunkManager.releaseTicket(t);
		}
		
		FTBLib.dev_logger.info("Loading tickets: " + ticketNames);
		*/
	}
	
	@SubscribeEvent
	public void onChunkForced(ForgeChunkManager.ForceChunkEvent e)
	{
		//FTBLib.dev_logger.info("Chunk from " + e.ticket.getModId() +  " forced " + e.location);
	}
	
	@SubscribeEvent
	public void onChunkUnforced(ForgeChunkManager.UnforceChunkEvent e)
	{
		//FTBLib.dev_logger.info("Chunk from " + e.ticket.getModId() + " unforced " + e.location);
	}
	
	@SubscribeEvent
	public void worldUnloadEvent(WorldEvent.Unload e)
	{
		/*
		if(!e.world.isRemote && LMWorldServer.inst != null)
		{
			for(LMPlayer p : LMWorldServer.inst.players)
				p.toPlayerMP().unloadAllChunks(e.world);
			
			Set<Long> keys = instance.tickets.keySet();
			
			for(Long l : keys)
			{
				if(Bits.intFromLongA(l.longValue()) == e.world.provider.dimensionId)
				{
					ForgeChunkManager.releaseTicket(instance.tickets.get(l));
					instance.tickets.remove(l);
				}
			}
		}
		*/
	}
	
	public static void worldLoadEvent(World w)
	{
		if(w != null && !w.isRemote && LMWorldServer.inst != null)
			for(LMPlayer p : LMWorldServer.inst.players)
				p.toPlayerMP().loadAllChunks(w);
	}
}