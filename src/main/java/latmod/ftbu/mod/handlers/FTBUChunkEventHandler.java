package latmod.ftbu.mod.handlers;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.FTBLib;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.world.WorldEvent;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	private final FastMap<Long, ForgeChunkManager.Ticket> tickets = new FastMap<Long, ForgeChunkManager.Ticket>();
	
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
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		FTBLib.dev_logger.info("Loading tickets: " + tickets);
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
			for(LMPlayer p : LMWorldServer.inst.players)
				p.toPlayerMP().unloadAllChunks(e.world);
	}
	
	public static void worldLoadEvent(World w)
	{
		if(w != null && !w.isRemote && LMWorldServer.inst != null)
			for(LMPlayer p : LMWorldServer.inst.players)
				p.toPlayerMP().loadAllChunks(w);
		
		for(int i = instance.tickets.size() - 1; i >= 0; i--)
		{
			Long l = instance.tickets.keys.get(i);
			if(Bits.intFromLongA(l) == w.provider.dimensionId)
			{
				ForgeChunkManager.releaseTicket(instance.tickets.get(l));
				instance.tickets.remove(l);
			}
		}
	}
}