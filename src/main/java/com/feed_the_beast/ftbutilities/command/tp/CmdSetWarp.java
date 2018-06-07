package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdSetWarp extends CmdBase
{
	public CmdSetWarp()
	{
		super("setwarp", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 5)
		{
			return getListOfStringsMatchingLastWord(args, CommandUtils.getDimensionNames());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 1);
		BlockDimPos pos;

		args[0] = args[0].toLowerCase();

		if (args.length == 2)
		{
			pos = new BlockDimPos(CommandUtils.getForgePlayer(sender, args[1]).getCommandPlayer(sender));
		}
		else if (args.length >= 4)
		{
			int x = parseInt(args[1]);
			int y = parseInt(args[2]);
			int z = parseInt(args[3]);
			pos = new BlockDimPos(x, y, z, args.length >= 5 ? parseInt(args[4]) : sender.getEntityWorld().provider.getDimension());
		}
		else
		{
			pos = new BlockDimPos(sender);
		}

		FTBUtilitiesUniverseData.WARPS.set(args[0], pos);
		sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.warps.set", args[0]));
		Universe.get().markDirty();
	}
}