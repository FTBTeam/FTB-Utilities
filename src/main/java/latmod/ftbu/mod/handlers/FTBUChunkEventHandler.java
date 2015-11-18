package latmod.ftbu.mod.handlers;

import java.util.List;

import com.google.common.collect.ListMultimap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.FTBLib;
import ftb.lib.mod.FTBLibFinals;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.world.WorldEvent;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback, ForgeChunkManager.OrderedLoadingCallback, ForgeChunkManager.PlayerOrderedLoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		if(FTBLibFinals.DEV) FTBLib.logger.info("Loading tickets");
		//Ticket t = ForgeChunkManager.requestTicket(FTBU.inst, world, ForgeChunkManager.Type.NORMAL);
		//t.setChunkListDepth(1000);
		//tickets.add(t);
	}
	
	public ListMultimap<String, ForgeChunkManager.Ticket> playerTicketsLoaded(ListMultimap<String, ForgeChunkManager.Ticket> tickets, World world)
	{
		if(FTBLibFinals.DEV) FTBLib.logger.info("Loading tickets for players");
		return tickets;
	}
	
	public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount)
	{
		if(FTBLibFinals.DEV) FTBLib.logger.info("Loading tickets for world");
		return tickets;
	}
	
	@SubscribeEvent
	public void onChunkForced(ForgeChunkManager.ForceChunkEvent e)
	{
		if(FTBLibFinals.DEV) FTBLib.logger.info("Chunk forced " + e.location);
	}
	
	@SubscribeEvent
	public void onChunkUnforced(ForgeChunkManager.UnforceChunkEvent e)
	{
		if(FTBLibFinals.DEV) FTBLib.logger.info("Chunk unforced " + e.location);
	}
	
	@SubscribeEvent
	public void worldLoadEvent(WorldEvent.Load e)
	{
		/*if(!e.world.isRemote)
		{
			ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(this, e.world, ForgeChunkManager.Type.NORMAL);
			if(ticket != null)
			{
				ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(0, 0));
			}
		}*/
	}
	
	@SubscribeEvent
	public void worldUnloadEvent(WorldEvent.Unload e)
	{
	}
}