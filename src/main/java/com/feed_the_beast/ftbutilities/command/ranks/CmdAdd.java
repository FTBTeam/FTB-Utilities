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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdAdd extends CmdBase
{
	public CmdAdd()
	{
		super("add", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if ((args.length == 1 || args.length == 2) && Ranks.isActive())
		{
			List<String> list = new ArrayList<>();

			if (args.length == 1)
			{
				list.addAll(Arrays.asList(server.getPlayerList().getOnlinePlayerNames()));
			}

			list.addAll(Ranks.INSTANCE.getRankNames(false));
			return getListOfStringsMatchingLastWord(args, list);
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
			throw FTBLib.errorFeatureDisabledServer(sender);
		}

		checkArgs(sender, args, 2);
		Rank rank = Ranks.INSTANCE.getRank(server, sender, args[0]);
		Rank parent = Ranks.INSTANCE.getRank(server, sender, args[1]);

		if (rank.addParent(parent))
		{
			rank.ranks.save();
			sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.add.text", parent.getDisplayName(), rank.getDisplayName()));
		}
	}
}