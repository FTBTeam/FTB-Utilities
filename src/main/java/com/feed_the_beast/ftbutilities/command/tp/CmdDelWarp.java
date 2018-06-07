package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
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
			return getListOfStringsMatchingLastWord(args, FTBUtilitiesUniverseData.WARPS.list());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 1);

		args[0] = args[0].toLowerCase();

		if (FTBUtilitiesUniverseData.WARPS.set(args[0], null))
		{
			sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.warps.del", args[0]));
			Universe.get().markDirty();
		}
		else
		{
			throw FTBUtilities.error(sender, "ftbutilities.lang.warps.not_set", args[0]);
		}
	}
}