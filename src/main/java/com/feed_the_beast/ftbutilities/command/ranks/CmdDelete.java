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
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdDelete extends CmdBase
{
	public CmdDelete()
	{
		super("delete", Level.OP);
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("del");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1 && Ranks.isActive())
		{
			return getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.getRankNames(false));
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!Ranks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		checkArgs(sender, args, 1);

		Rank rank = Ranks.INSTANCE.getRank(server, sender, args[0]);

		if (rank.remove())
		{
			rank.ranks.save();
			sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.delete.deleted", rank.getDisplayName()));
		}
		else
		{
			sender.sendMessage(FTBLib.lang(sender, "nothing_changed"));
		}
	}
}