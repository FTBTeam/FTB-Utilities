package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class CmdLoadedChunks extends CmdBase
{
	public CmdLoadedChunks()
	{
		super("loaded_chunks_list", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
		GuidePage page = new GuidePage("loaded_chunks").setTitle(StringUtils.text("Loaded Chunks")); // TODO: Lang

		for (WorldServer w : DimensionManager.getWorlds())
		{
			ImmutableSetMultimap<ChunkPos, ForgeChunkManager.Ticket> map = ForgeChunkManager.getPersistentChunksFor(w);

			Map<String, Collection<ChunkPos>> chunksMap = new HashMap<>();

			for (ForgeChunkManager.Ticket t : map.values())
			{
				Collection<ChunkPos> list = chunksMap.get(t.getModId());
				if (list == null)
				{
					chunksMap.put(t.getModId(), list = new HashSet<>());
				}
				for (ChunkPos c : t.getChunkList())
				{
					if (!list.contains(c))
					{
						list.add(c);
					}
				}
			}

			int dimId = w.provider.getDimension();
			GuidePage dim = page.getSub(Integer.toString(dimId)).setTitle(StringUtils.text(w.provider.getDimensionType().getName() + " [" + (dimId == 0 ? "DIM0" : w.provider.getSaveFolder()) + "]"));

			for (Map.Entry<String, Collection<ChunkPos>> e1 : chunksMap.entrySet())
			{
				GuidePage mod = dim.getSub(e1.getKey()).setTitle(StringUtils.text(e1.getKey() + " [" + e1.getValue().size() + "]"));
				for (ChunkPos c : e1.getValue())
				{
					StringBuilder owner = new StringBuilder();

					IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(new ChunkDimPos(c, dimId));

					if (chunk != null)
					{
						owner = new StringBuilder(chunk.getOwner().getName());
					}
					else
					{
						for (ForgeChunkManager.Ticket t : map.get(c))
						{
							if (t.isPlayerTicket())
							{
								if (owner.length() > 0)
								{
									owner.append(", ");
								}

								owner.append(t.getPlayerName());
							}
						}
					}

					if (owner.length() == 0)
					{
						owner = new StringBuilder("Unknown");
					}

					ITextComponent line = StringUtils.text(c.x + ", " + c.z + " [" + owner + "]");
					int cx = MathUtils.chunk(c.x) + 8;
					int cz = MathUtils.chunk(c.z) + 8;
					String cmd = "/tp " + cx + " " + ep.world.getHeight(cx, cz) + " " + cz;
					line.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, StringUtils.text(cmd)));
					line.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
					mod.println(line);
				}
			}
		}

		page.cleanup();
		FTBLibAPI.API.displayGuide(ep, page);
	}
}