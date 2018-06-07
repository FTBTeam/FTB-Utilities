package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
public class CmdClaim extends CmdBase
{
	public CmdClaim()
	{
		super("claim", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!ClaimedChunks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ForgePlayer p = CommandUtils.getForgePlayer(player);
		boolean checkLimits = true;

		if (args.length >= 1)
		{
			ForgePlayer p1 = CommandUtils.getForgePlayer(sender, args[0]);

			if (p != p1 && !PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.CLAIMS_OTHER_CLAIM))
			{
				throw new CommandException("commands.generic.permission");
			}

			p = p1;
			checkLimits = false;
		}

		ChunkDimPos pos = new ChunkDimPos(player);

		if (checkLimits && !ClaimedChunks.instance.canPlayerModify(p, pos, FTBUtilitiesPermissions.CLAIMS_OTHER_CLAIM))
		{
			FTBUtilitiesNotifications.sendCantModifyChunk(server, player);
			return;
		}

		switch (ClaimedChunks.instance.claimChunk(p, pos, checkLimits))
		{
			case SUCCESS:
				Notification.of(FTBUtilitiesNotifications.CHUNK_MODIFIED, FTBUtilities.lang(player, "ftbutilities.lang.chunks.chunk_claimed")).send(server, player);
				CmdChunks.updateChunk(player, pos);
				break;
			case DIMENSION_BLOCKED:
				Notification.of(FTBUtilitiesNotifications.CHUNK_CANT_CLAIM, FTBUtilities.lang(player, "ftbutilities.lang.chunks.claiming_not_enabled_dim")).setError().send(server, player);
				break;
			case NO_POWER:
				break;
			default:
				FTBUtilitiesNotifications.sendCantModifyChunk(server, player);
				break;
		}
	}
}