package latmod.ftbu.mod.handlers;

import java.util.List;

import com.google.common.collect.ListMultimap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.FTBLib;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class FTBUChunkEventHandler implements ForgeChunkManager.OrderedLoadingCallback, ForgeChunkManager.PlayerOrderedLoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		FTBLib.logger.info("Loading tickets");
		//Ticket t = ForgeChunkManager.requestTicket(FTBU.inst, world, ForgeChunkManager.Type.NORMAL);
		//t.setChunkListDepth(1000);
		//tickets.add(t);
	}
	
	public ListMultimap<String, Ticket> playerTicketsLoaded(ListMultimap<String, Ticket> tickets, World world)
	{
		FTBLib.logger.info("Loading tickets for players");
		return tickets;
	}
	
	public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount)
	{
		FTBLib.logger.info("Loading tickets for world");
		return tickets;
	}
	
	@SubscribeEvent
	public void onChunkForced(ForgeChunkManager.ForceChunkEvent e)
	{
		FTBLib.logger.info("Chunk forced " + e.location);
	}
	
	@SubscribeEvent
	public void onChunkUnforced(ForgeChunkManager.UnforceChunkEvent e)
	{
		FTBLib.logger.info("Chunk unforced " + e.location);
	}
}