package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
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
		if (Ranks.INSTANCE == null)
		{
			throw new CommandException("feature_disabled_server");
		}

		checkArgs(sender, args, 2);

		String id = args[0].toLowerCase();

		if (Ranks.INSTANCE.getRankNames().contains(id))
		{
			throw new CommandException("ftbutilities.lang.rank.id_exists", id);
		}

		Rank parent = args.length == 1 ? Ranks.INSTANCE.builtinPlayerRank : Ranks.INSTANCE.getRank(args[1], null);

		if (parent == null)
		{
			throw new CommandException("ftbutilities.lang.rank.not_found", id);
		}

		Ranks.INSTANCE.addRank(new Rank(Ranks.INSTANCE, id, parent));
	}
}