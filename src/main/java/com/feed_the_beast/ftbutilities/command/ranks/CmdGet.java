package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
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
		super("get", Level.ALL);
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

		ForgePlayer p = CommandUtils.getSelfOrOther(sender, args, 0);
		Rank rank = Ranks.INSTANCE.getRank(p.team.universe.server, p.getProfile(), p.getContext());
		sender.sendMessage(new TextComponentString("").appendSibling(StringUtils.color(p.getDisplayName(), TextFormatting.BLUE)).appendText(" - ").appendSibling(rank.getDisplayName()));
	}
}