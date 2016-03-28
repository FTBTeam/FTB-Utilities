package ftb.utils.cmd.admin;

import com.google.common.collect.ImmutableSetMultimap;
import ftb.lib.api.cmd.*;
import ftb.lib.api.info.InfoPage;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import java.util.*;

public class CmdLoadedChunks extends CommandLM
{
	public CmdLoadedChunks()
	{ super("loaded_chunks", CommandLevel.OP); }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		InfoPage file = new InfoPage("Loaded Chunks");
		
		for(WorldServer w : DimensionManager.getWorlds())
		{
			ImmutableSetMultimap<ChunkCoordIntPair, Ticket> map = ForgeChunkManager.getPersistentChunksFor(w);
			
			HashMap<String, ArrayList<ChunkCoordIntPair>> chunksMap = new HashMap<>();
			
			for(Ticket t : map.values())
			{
				ArrayList<ChunkCoordIntPair> list = chunksMap.get(t.getModId());
				if(list == null) chunksMap.put(t.getModId(), list = new ArrayList<>());
				for(ChunkCoordIntPair c : t.getChunkList())
					if(!list.contains(c)) list.add(c);
			}
			
			InfoPage dim = file.getSub(w.provider.getDimensionName());
			
			for(Map.Entry<String, ArrayList<ChunkCoordIntPair>> e1 : chunksMap.entrySet())
			{
				InfoPage mod = dim.getSub(e1.getKey() + " [" + e1.getValue().size() + "]");
				for(ChunkCoordIntPair c : e1.getValue())
					mod.printlnText(c.chunkXPos + ", " + c.chunkZPos + " [ " + c.getCenterXPos() + ", " + c.getCenterZPosition() + " ]");
			}
		}
		
		file.displayGuide(ep);
	}
}