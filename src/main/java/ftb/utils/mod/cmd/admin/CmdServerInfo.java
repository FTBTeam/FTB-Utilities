package ftb.utils.mod.cmd.admin;

import com.google.common.collect.ImmutableSetMultimap;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.lib.api.info.InfoPage;
import latmod.lib.IntList;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CmdServerInfo extends CommandLM
{
	public CmdServerInfo()
	{ super("server_info", CommandLevel.OP); }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		InfoPage file = new InfoPage("server_info").setTitle(new ChatComponentTranslation("player_action.ftbu.server_info"));
		file.useUnicodeFont = Boolean.FALSE;
		
		InfoPage page = file.getSub("Entities");
		
		Set<Integer> entityIDset = EntityList.IDtoClassMapping.keySet();
		for(Integer i : entityIDset)
			page.printlnText("[" + i + "] " + EntityList.getStringFromID(i.intValue()));
		
		page = file.getSub("Enchantments");
		
		IntList freeIDs = new IntList();
		
		for(int i = 0; i < 256; i++)
		{
			Enchantment e = Enchantment.enchantmentsList[i];
			if(e == null) freeIDs.add(i);
			else page.printlnText("[" + i + "] " + e.getTranslatedName(1));
		}
		
		page.printlnText("Empty IDs: " + freeIDs.toString());
		
		page = file.getSub("loaded_chunks").setTitle(new ChatComponentText("Loaded Chunks"));//TODO: Lang
		
		for(WorldServer w : DimensionManager.getWorlds())
		{
			ImmutableSetMultimap<ChunkCoordIntPair, ForgeChunkManager.Ticket> map = ForgeChunkManager.getPersistentChunksFor(w);
			
			Map<String, List<ChunkCoordIntPair>> chunksMap = new HashMap<>();
			
			for(ForgeChunkManager.Ticket t : map.values())
			{
				List<ChunkCoordIntPair> list = chunksMap.get(t.getModId());
				if(list == null) chunksMap.put(t.getModId(), list = new ArrayList<>());
				for(ChunkCoordIntPair c : t.getChunkList())
					if(!list.contains(c)) list.add(c);
			}
			
			InfoPage dim = page.getSub(w.provider.getDimensionName());
			
			for(Map.Entry<String, List<ChunkCoordIntPair>> e1 : chunksMap.entrySet())
			{
				InfoPage mod = dim.getSub(e1.getKey() + " [" + e1.getValue().size() + "]");
				for(ChunkCoordIntPair c : e1.getValue())
					mod.printlnText(c.chunkXPos + ", " + c.chunkZPos + " [ " + c.getCenterXPos() + ", " + c.getCenterZPosition() + " ]");
			}
		}
		
		file.displayGuide(ep);
	}
}