package com.feed_the_beast.ftbu.cmd.client;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdToggleGamemode extends CmdBase
{
	public CmdToggleGamemode()
	{
		super("toggle_gamemode", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		ClientUtils.execClientCommand("/gamemode " + (ClientUtils.MC.player.capabilities.isCreativeMode ? "survival" : "creative"));
	}
}