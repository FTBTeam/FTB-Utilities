package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class CmdGet extends CmdBase
{
	public CmdGet()
	{
		super("get", Level.OP);
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
			throw new CommandException("feature_disabled_server");
		}

		checkArgs(sender, args, 1);
		ForgePlayer p = getSelfOrOther(sender, args, 0);
		Rank rank = Ranks.INSTANCE.getRank(p.team.universe.server, p.getProfile(), p.getContext());

		if (rank == null)
		{
			throw new CommandException("commands.ranks.not_found", args[0]);
		}

		sender.sendMessage(new TextComponentString("").appendSibling(StringUtils.color(p.getDisplayName(), TextFormatting.BLUE)).appendText(" - ").appendSibling(StringUtils.color(new TextComponentString(rank.getName()), TextFormatting.DARK_GREEN)));
	}
}