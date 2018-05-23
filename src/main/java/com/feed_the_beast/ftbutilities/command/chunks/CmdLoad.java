package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdLoad extends CmdBase
{
	public CmdLoad()
	{
		super("load", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (ClaimedChunks.instance == null)
		{
			throw new CommandException("feature_disabled_server");
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ForgePlayer p = getForgePlayer(player);
		ChunkDimPos pos = new ChunkDimPos(player);

		if (p.hasTeam() && ClaimedChunks.instance.canPlayerModify(p, pos) && ClaimedChunks.instance.loadChunk(p.team, pos))
		{
			Notification.of(FTBUtilitiesNotifications.CHUNK_MODIFIED, FTBUtilities.lang(player, "ftbutilities.lang.chunks.chunk_loaded")).send(server, player);
			CmdChunks.updateChunk(player, pos);
		}
		else
		{
			FTBUtilitiesNotifications.sendCantModifyChunk(server, player);
		}
	}
}