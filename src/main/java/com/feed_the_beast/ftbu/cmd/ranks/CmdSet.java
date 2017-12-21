package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.ranks.DefaultOPRank;
import com.feed_the_beast.ftbu.ranks.DefaultPlayerRank;
import com.feed_the_beast.ftbu.ranks.Rank;
import com.feed_the_beast.ftbu.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdSet extends CmdBase
{
	public CmdSet()
	{
		super("set", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 0;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Ranks.getRankNames());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 2);

		Rank r = (args[1].equalsIgnoreCase("none") || args[1].equals("-")) ? null : Ranks.getRank(args[1], null);

		if (r == DefaultPlayerRank.INSTANCE)
		{
			FTBULang.RANK_USE_DEOP.sendMessage(sender, args[0]);
			return;
		}
		else if (r == DefaultOPRank.INSTANCE)
		{
			FTBULang.RANK_USE_OP.sendMessage(sender, args[0]);
			return;
		}
		else if (!Ranks.getRankNames().contains(args[1]))
		{
			throw FTBULang.RANK_NOT_FOUND.commandError(args[1]);
		}

		ForgePlayer p = getForgePlayer(args[0]);
		Ranks.setRank(p.getId(), r);

		if (r == null)
		{
			FTBULang.RANK_UNSET.sendMessage(sender, p.getName());
		}
		else
		{
			FTBULang.RANK_SET.sendMessage(sender, p.getName(), r.getName());
		}
	}
}
