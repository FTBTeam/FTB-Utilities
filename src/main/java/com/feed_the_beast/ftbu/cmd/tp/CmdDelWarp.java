package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdDelWarp extends CmdBase
{
	public CmdDelWarp()
	{
		super("delwarp", Level.OP);
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
		checkArgs(sender, args, 1);

		args[0] = args[0].toLowerCase();

		if (FTBUUniverseData.WARPS.set(args[0], null))
		{
			FTBULang.WARP_DEL.sendMessage(sender, args[0]);
		}
		else
		{
			throw FTBULang.WARP_NOT_SET.commandError(args[0]);
		}
	}
}