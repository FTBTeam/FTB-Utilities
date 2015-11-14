package latmod.ftbu.mod.cmd.admin;

import java.util.Iterator;

import com.google.common.collect.ImmutableSetMultimap;

import ftb.lib.FTBLib;
import latmod.ftbu.cmd.*;
import latmod.lib.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class CmdAdminChunks extends CommandLM
{
	public CmdAdminChunks(String s)
	{ super(s, CommandLevel.OP); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		ImmutableSetMultimap<ChunkCoordIntPair, Ticket> map = ForgeChunkManager.getPersistentChunksFor(ics.getEntityWorld());
		
		if(map.isEmpty()) return null;
		
		Iterator<ChunkCoordIntPair> keys = map.keySet().iterator();
		Iterator<Ticket> values = map.values().iterator();
		
		FastMap<String, FastList<ChunkCoordIntPair>> map1 = new FastMap<String, FastList<ChunkCoordIntPair>>();
		
		while(keys.hasNext())
		{
			ChunkCoordIntPair k = keys.next();
			Ticket t = values.next();
			
			FastList<ChunkCoordIntPair> list = map1.get(t.getModId());
			
			if(list == null)
			{
				list = new FastList<ChunkCoordIntPair>();
				map1.put(t.getModId(), list);
			}
			
			list.add(k);
		}
		
		for(int i = 0; i < map1.size(); i++)
			FTBLib.printChat(ics, map1.values.get(i).size() + " chunks from " + map1.keys.get(i));
		
		return null;
	}
}