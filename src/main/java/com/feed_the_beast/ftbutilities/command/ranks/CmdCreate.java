package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdCreate extends CmdBase
{
	public CmdCreate()
	{
		super("create", Level.OP);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!Ranks.isActive())
		{
			throw FTBLib.errorFeatureDisabledServer(sender);
		}

		checkArgs(sender, args, 1);

		if (!Ranks.isValidName(args[0]))
		{
			throw FTBUtilities.error(sender, "commands.ranks.create.id_invalid", args[0]);
		}
		else if (Ranks.INSTANCE.getRank(args[0]) != null)
		{
			throw FTBUtilities.error(sender, "commands.ranks.create.id_exists", args[0]);
		}

		Rank rank = new Rank(Ranks.INSTANCE, args[0]);

		if (args.length > 1)
		{
			for (int i = 1; i < args.length; i++)
			{
				rank.addParent(Ranks.INSTANCE.getRank(server, sender, args[1].toLowerCase()));
			}
		}

		if (rank.add())
		{
			rank.ranks.save();
			sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.create.added", rank.getDisplayName()));
		}
	}
}