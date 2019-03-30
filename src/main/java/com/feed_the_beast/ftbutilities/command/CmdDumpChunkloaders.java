package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.HashSet;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class CmdDumpChunkloaders extends CmdBase
{
	public CmdDumpChunkloaders()
	{
		super("dump_chunkloaders", Level.OP_OR_SP);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		for (World world : DimensionManager.getWorlds())
		{
			HashSet<ForgeChunkManager.Ticket> set = new HashSet<>();

			for (Map.Entry<ChunkPos, ForgeChunkManager.Ticket> entry : ForgeChunkManager.getPersistentChunksFor(world).entries())
			{
				set.add(entry.getValue());
			}

			if (!set.isEmpty())
			{
				sender.sendMessage(new TextComponentString("- DIM " + world.provider.getDimension() + ":"));

				for (ForgeChunkManager.Ticket ticket : set)
				{
					ITextComponent title = new TextComponentString(String.format("#%08x", ticket.hashCode()));
					title.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(ticket.getChunkList().size() + " chunks")));

					ITextComponent owner = new TextComponentString("Owner");
					owner.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(ticket.getModId() + " : " + ticket.getEntity())));

					ITextComponent data = new TextComponentString("Data");
					data.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(NBTUtils.getColoredNBTString(ticket.getModData()))));

					ITextComponent chunks = new TextComponentString("Chunks");

					int minX = Integer.MAX_VALUE;
					int minZ = Integer.MAX_VALUE;
					int maxX = Integer.MIN_VALUE;
					int maxZ = Integer.MIN_VALUE;

					for (ChunkPos pos : ticket.getChunkList())
					{
						if (pos.x < minX)
						{
							minX = pos.x;
						}

						if (pos.z < minZ)
						{
							minZ = pos.z;
						}

						if (pos.x > maxX)
						{
							maxX = pos.x;
						}

						if (pos.z > maxZ)
						{
							maxZ = pos.z;
						}
					}

					BlockPos pos = new BlockPos((minX + maxX) * 8 + 8, 255, (minZ + maxZ) * 8 + 8);
					world.getChunk(pos);
					pos = world.getTopSolidOrLiquidBlock(pos);
					chunks.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(pos + " ; " + ticket.getChunkList().toString())));
					chunks.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + pos.getX() + " " + pos.getY() + " " + pos.getZ()));

					sender.sendMessage(new TextComponentString("").appendSibling(title).appendText(" | ").appendSibling(owner).appendText(" | ").appendSibling(data).appendText(" | ").appendSibling(chunks));
				}
			}
		}
	}
}