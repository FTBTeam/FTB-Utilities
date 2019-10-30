package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdRemove extends CmdBase
{
	public CmdRemove()
	{
		super("remove", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if ((args.length == 1 || args.length == 2) && Ranks.isActive())
		{
			return getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.getRankNames(false));
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!Ranks.isActive())
		{
			throw FTBLib.errorFeatureDisabledServer(sender);
		}

		checkArgs(sender, args, 1);
		Rank rank = Ranks.INSTANCE.getRank(args[0]);

		if (rank == null)
		{
			throw FTBUtilities.error(sender, "commands.ranks.create.id_invalid", args[0]);
		}

		Rank parent = Ranks.INSTANCE.getRank(args[1]);

		if (parent == null)
		{
			throw FTBUtilities.error(sender, "commands.ranks.create.id_invalid", args[1]);
		}

		if (rank.removeParent(parent))
		{
			rank.ranks.save();
			sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.remove.text", parent.getDisplayName(), rank.getDisplayName()));
		}
	}
}