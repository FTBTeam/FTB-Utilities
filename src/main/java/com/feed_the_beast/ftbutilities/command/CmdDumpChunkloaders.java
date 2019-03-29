package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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

					ITextComponent chunks = new TextComponentString("Chunks");
					chunks.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(ticket.getChunkList().toString())));

					sender.sendMessage(new TextComponentString("").appendSibling(title).appendText(" | ").appendSibling(owner).appendText(" | ").appendSibling(chunks));
				}
			}
		}
	}
}