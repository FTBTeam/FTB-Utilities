package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.ranks.DefaultPlayerRank;
import com.feed_the_beast.ftbu.ranks.Rank;
import com.feed_the_beast.ftbu.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 2);

		String id = args[0].toLowerCase();

		if (Ranks.getRankNames().contains(id))
		{
			throw FTBULang.RANK_ID_EXISTS.commandError(id);
		}

		Rank parent = args.length == 1 ? DefaultPlayerRank.INSTANCE : Ranks.getRank(args[1], null);

		if (parent == null)
		{
			throw FTBULang.RANK_NOT_FOUND.commandError(id);
		}

		Ranks.addRank(new Rank(id, parent));
	}
}