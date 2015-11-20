package latmod.ftbu.mod.handlers;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	
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
				p.toPlayerMP().claims.unloadChunks(e.world);
	}*/
	
	public static void worldLoadEvent(World w)
	{
	//	if(w != null && !w.isRemote && LMWorldServer.inst != null)
	//		for(LMPlayer p : LMWorldServer.inst.players)
	//			p.toPlayerMP().claims.loadChunks(w);
	}
}