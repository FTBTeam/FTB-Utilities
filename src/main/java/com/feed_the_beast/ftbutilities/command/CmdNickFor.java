package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.net.MessageUpdateTabName;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

public class CmdNickFor extends CmdBase
{
	public CmdNickFor()
	{
		super("nickfor", Level.OP);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 2);
		ForgePlayer player = CommandUtils.getForgePlayer(sender, args[0]);

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(player);
		data.setNickname(StringUtils.joinSpaceUntilEnd(1, args).trim());

		if (data.getNickname().isEmpty())
		{
			sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.nickname_reset"));
		}
		else
		{
			String name = StringUtils.addFormatting(data.getNickname());

			if (name.indexOf(StringUtils.FORMATTING_CHAR) != -1)
			{
				name += TextFormatting.RESET;
			}

			sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.nickname_changed", name));
		}

		new MessageUpdateTabName(player.getPlayer()).sendToAll();
	}
}