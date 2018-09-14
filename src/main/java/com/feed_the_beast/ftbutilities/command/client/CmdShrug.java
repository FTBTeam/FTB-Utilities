package com.feed_the_beast.ftbutilities.command.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdShrug extends CmdBase
{
	public CmdShrug()
	{
		super("shrug", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		String txt = StringJoiner.with(' ').joinStrings(args);

		if (txt.isEmpty())
		{
			ClientUtils.execClientCommand("\u00AF\\_(\u30C4)_/\u00AF");
		}
		else
		{
			ClientUtils.execClientCommand(txt + " \u00AF\\_(\u30C4)_/\u00AF");
		}
	}
}