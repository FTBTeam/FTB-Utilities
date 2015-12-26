package latmod.ftbu.mod.cmd.admin;

import com.google.common.collect.ImmutableSetMultimap;
import ftb.lib.cmd.*;
import latmod.ftbu.api.guide.*;
import latmod.ftbu.util.LatCoreMC;
import latmod.lib.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import java.util.Map;

public class CmdLoadedChunks extends CommandLM
{
	public CmdLoadedChunks()
	{ super("loaded_chunks", CommandLevel.OP); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		GuideFile file = new GuideFile(new ChatComponentText("Loaded Chunks"));
		
		for(WorldServer w : DimensionManager.getWorlds())
		{
			ImmutableSetMultimap<ChunkCoordIntPair, Ticket> map = ForgeChunkManager.getPersistentChunksFor(w);
			
			FastMap<String, FastList<ChunkCoordIntPair>> chunksMap = new FastMap<String, FastList<ChunkCoordIntPair>>();
			
			for(Ticket t : map.values())
			{
				FastList<ChunkCoordIntPair> list = chunksMap.get(t.getModId());
				if(list == null) chunksMap.put(t.getModId(), list = new FastList<ChunkCoordIntPair>());
				for(ChunkCoordIntPair c : t.getChunkList())
					if(!list.contains(c)) list.add(c);
			}
			
			GuideCategory dim = file.main.getSub(new ChatComponentText(w.provider.getDimensionName()));
			
			for(Map.Entry<String, FastList<ChunkCoordIntPair>> e1 : chunksMap.entrySet())
			{
				GuideCategory mod = dim.getSub(new ChatComponentText(e1.getKey() + " [" + e1.getValue().size() + "]"));
				for(ChunkCoordIntPair c : e1.getValue())
					mod.println(c.chunkXPos + ", " + c.chunkZPos + " [ " + c.getCenterXPos() + ", " + c.getCenterZPosition() + " ]");
			}
		}
		
		LatCoreMC.displayGuide(ep, file);
		return null;
	}
}