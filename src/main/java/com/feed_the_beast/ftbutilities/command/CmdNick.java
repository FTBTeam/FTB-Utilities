package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CmdNick extends CmdBase
{
	public CmdNick()
	{
		super("nick", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		ForgePlayer player = getForgePlayer(sender);

		if (!player.hasPermission(FTBUtilitiesPermissions.NICKNAME))
		{
			throw new CommandException("commands.generic.permission");
		}

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(player);
		data.setNickname(StringUtils.joinSpaceUntilEnd(0, args));

		if (data.getNickname().isEmpty())
		{
			player.getPlayer().sendStatusMessage(FTBUtilities.lang(player.getPlayer(), "ftbutilities.lang.nickname_reset"), true);
		}
		else
		{
			player.getPlayer().sendStatusMessage(FTBUtilities.lang(player.getPlayer(), "ftbutilities.lang.nickname_changed", data.getNickname()), true);
		}
	}
}