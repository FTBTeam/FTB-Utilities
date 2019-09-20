package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;

/**
 * @author LatvianModder
 */
public class CmdUnclaimAll extends CmdBase
{
	public CmdUnclaimAll()
	{
		super("unclaim_all", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, CommandUtils.getDimensionNames());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 1;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!ClaimedChunks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		ForgePlayer p = CommandUtils.getSelfOrOther(sender, args, 1, FTBUtilitiesPermissions.CLAIMS_OTHER_UNCLAIM);

		if (p.hasTeam())
		{
			OptionalInt dimension = CommandUtils.parseDimension(sender, args, 0);
			ClaimedChunks.instance.unclaimAllChunks(p, p.team, dimension);
			Notification.of(FTBUtilitiesNotifications.UNCLAIMED_ALL, FTBUtilities.lang(sender, "ftbutilities.lang.chunks.unclaimed_all")).send(server, sender);
		}
		else
		{
			throw FTBLib.error(sender, "ftblib.lang.team.error.no_team");
		}
	}
}