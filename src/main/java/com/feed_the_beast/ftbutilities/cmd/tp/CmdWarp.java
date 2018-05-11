package com.feed_the_beast.ftbutilities.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import com.feed_the_beast.ftbutilities.net.MessageSendWarpList;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.TextComponentHelper;

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

		if (args[0].equals("gui"))
		{
			new MessageSendWarpList(player).sendTo(player);
			return;
		}

		BlockDimPos p = FTBUtilitiesUniverseData.WARPS.get(args[0]);
		if (p == null)
		{
			throw new CommandException("ftbutilities.lang.warps.not_set", args[0]);
		}
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(getForgePlayer(player));
		long cooldown = data.getWarpCooldown();
		if (cooldown > 0)
		{
			throw new CommandException("ftbutilities.lang.warps.in_cooldown", StringUtils.getTimeStringTicks(cooldown));
		}
		ServerUtils.teleportEntity(player, p);
		data.setLastWarp(server.getWorld(0).getTotalWorldTime());
		sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "ftbutilities.lang.warps.tp", args[0]));
	}
}