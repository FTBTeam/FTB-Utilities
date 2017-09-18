package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.net.MessageSendWarpList;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

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
			return getListOfStringsMatchingLastWord(args, FTBUUniverseData.WARPS.list());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(args, 1, "<warp>");

		args[0] = args[0].toLowerCase();

		if (args[0].equals("list"))
		{
			Collection<String> list = FTBUUniverseData.WARPS.list();
			sender.sendMessage(new TextComponentString(list.isEmpty() ? "-" : StringUtils.strip(list)));
			return;
		}

		EntityPlayerMP ep = getCommandSenderAsPlayer(sender);

		if (args[0].equals("gui"))
		{
			new MessageSendWarpList(ep).sendTo(ep);
			return;
		}

		BlockDimPos p = FTBUUniverseData.WARPS.get(args[0]);
		if (p == null)
		{
			throw FTBULang.WARP_NOT_SET.commandError(args[0]);
		}

		ServerUtils.teleportPlayer(ep, p);
		FTBULang.WARP_TP.sendMessage(sender, args[0]);
	}
}