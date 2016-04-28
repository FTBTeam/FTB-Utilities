package ftb.utils.cmd.admin;

import com.google.common.collect.ImmutableSetMultimap;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.lib.api.info.InfoPage;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by LatvianModder on 28.04.2016.
 */
public class CmdServerInfo extends CommandLM
{
	public CmdServerInfo()
	{ super("server_info", CommandLevel.OP); }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
		
		InfoPage serverInfo = new InfoPage("server_info").setTitle(new TextComponentTranslation(""));
		
		InfoPage page = serverInfo.getSub("loaded_chunks"); // TODO: Lang
		
		for(WorldServer w : DimensionManager.getWorlds())
		{
			ImmutableSetMultimap<ChunkCoordIntPair, ForgeChunkManager.Ticket> map = ForgeChunkManager.getPersistentChunksFor(w);
			
			Map<String, Collection<ChunkCoordIntPair>> chunksMap = new HashMap<>();
			
			for(ForgeChunkManager.Ticket t : map.values())
			{
				Collection<ChunkCoordIntPair> list = chunksMap.get(t.getModId());
				if(list == null) chunksMap.put(t.getModId(), list = new HashSet<>());
				for(ChunkCoordIntPair c : t.getChunkList())
				{
					if(!list.contains(c))
					{
						list.add(c);
					}
				}
			}
			
			InfoPage dim = page.getSub(w.provider.getDimensionType().getName());
			
			for(Map.Entry<String, Collection<ChunkCoordIntPair>> e1 : chunksMap.entrySet())
			{
				InfoPage mod = dim.getSub(e1.getKey() + " [" + e1.getValue().size() + "]");
				for(ChunkCoordIntPair c : e1.getValue())
					mod.printlnText(c.chunkXPos + ", " + c.chunkZPos + " [ " + c.getCenterXPos() + ", " + c.getCenterZPosition() + " ]");
			}
		}
		
		InfoPage list = serverInfo.getSub("entities"); //LANG
		
		for(String s : EntityList.stringToClassMapping.keySet())
		{
			list.printlnText("[" + EntityList.getIDFromString(s) + "] " + s);
		}
		
		list = serverInfo.getSub("enchantments"); //LANG
		
		for(Enchantment e : Enchantment.enchantmentRegistry)
		{
			list.printlnText("[" + e.getRegistryName() + "] " + e.getTranslatedName(1));
		}
		
		serverInfo.displayGuide(ep);
	}
}