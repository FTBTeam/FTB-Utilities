package latmod.ftbu.mod.handlers;

import java.util.List;

import latmod.ftbu.world.LMPlayerServer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	//private final FastMap<Long, ForgeChunkManager.Ticket> playerTickets = new FastMap<Long, ForgeChunkManager.Ticket>();
	
	public ForgeChunkManager.Ticket request(World w, LMPlayerServer player)
	{
		return null;
		
		/*
		Long l = Long.valueOf(Bits.intsToLong(w.provider.dimensionId, player.playerID));
		ForgeChunkManager.Ticket t = playerTickets.get(l);
		
		if(t == null)
		{
			t = ForgeChunkManager.requestPlayerTicket(FTBU.inst, player.getName(), w, ForgeChunkManager.Type.NORMAL);
			if(t == null) return null; else playerTickets.put(l, t);
		}
		
		return t;
		*/
	}
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		//FTBLib.dev_logger.info("Loading tickets");
	}
	
	/*
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
				p.toPlayerMP().claims.unloadAllChunks(e.world);
	}
	*/
	
	public static void worldLoadEvent(World w)
	{
		/*if(w != null && !w.isRemote && LMWorldServer.inst != null)
			for(LMPlayer p : LMWorldServer.inst.players)
				p.toPlayerMP().claims.loadAllChunks(w);
		
		for(int i = instance.playerTickets.size() - 1; i >= 0; i--)
		{
			Long l = instance.playerTickets.keys.get(i);
			if(Bits.intFromLongA(l) == w.provider.dimensionId)
			{
				ForgeChunkManager.releaseTicket(instance.playerTickets.get(l));
				instance.playerTickets.remove(l);
			}
		}*/
	}
}