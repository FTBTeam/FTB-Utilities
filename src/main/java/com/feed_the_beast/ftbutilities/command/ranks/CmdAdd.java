package com.feed_the_beast.ftbutilities.command.ranks;

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

		checkArgs(sender, args, 1);

		String id = args[0].toLowerCase();

		if (id.equals("none") || Ranks.INSTANCE.getRankNames().contains(id))
		{
			throw new CommandException("commands.ranks.add.id_exists", id);
		}

		Rank rank = new Rank(Ranks.INSTANCE, id);

		if (args.length == 2)
		{
			String pid = args[1].toLowerCase();
			rank.parent = Ranks.INSTANCE.getRank(pid);

			if (rank.parent == null)
			{
				throw new CommandException("commands.ranks.not_found", pid);
			}
		}

		Ranks.INSTANCE.addRank(rank);
	}
}