package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class CmdInfo extends CmdBase
{
	public CmdInfo()
	{
		super("info", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!ClaimedChunks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ChunkDimPos pos = new ChunkDimPos(player);
		ClaimedChunk chunk = ClaimedChunks.instance.getChunk(pos);

		ITextComponent owner;

		if (chunk == null)
		{
			owner = FTBUtilities.lang(sender, "commands.chunks.info.not_claimed");
		}
		else
		{
			owner = chunk.getTeam().getCommandTitle();
		}

		owner.getStyle().setColor(TextFormatting.GOLD);
		sender.sendMessage(FTBUtilities.lang(sender, "commands.chunks.info.text", pos.posX, pos.posZ, ServerUtils.getDimensionName(pos.dim), owner));
	}
}