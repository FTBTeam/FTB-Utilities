package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class CmdWarp extends CmdBase
{
	public CmdWarp()
	{
		super("warp", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUtilitiesUniverseData.WARPS.list());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 1);

		args[0] = args[0].toLowerCase();

		if (args[0].equals("list"))
		{
			Collection<String> list = FTBUtilitiesUniverseData.WARPS.list();
			sender.sendMessage(new TextComponentString(list.isEmpty() ? "-" : StringJoiner.with(", ").join(list)));
			return;
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if (Ranks.INSTANCE.getPermissionResult(player, Rank.NODE_COMMAND + ".ftbutilities.warp.teleport." + args[0], true) == Event.Result.DENY)
		{
			throw new CommandException("commands.generic.permission");
		}

		BlockDimPos p = FTBUtilitiesUniverseData.WARPS.get(args[0]);

		if (p == null)
		{
			throw FTBUtilities.error(sender, "ftbutilities.lang.warps.not_set", args[0]);
		}

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(CommandUtils.getForgePlayer(player));
		data.checkTeleportCooldown(sender, FTBUtilitiesPlayerData.Timer.WARP);
		FTBUtilitiesPlayerData.Timer.WARP.teleport(player, playerMP -> p.teleporter(), universe -> Notification.of(FTBUtilitiesNotifications.TELEPORT, FTBUtilities.lang(sender, "ftbutilities.lang.warps.tp", args[0])).send(server, player));
	}
}