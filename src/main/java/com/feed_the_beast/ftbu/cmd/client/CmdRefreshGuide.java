package com.feed_the_beast.ftbu.cmd.client;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.gui.guide.Guides;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdRefreshGuide extends CmdBase
{
	public CmdRefreshGuide()
	{
		super("refresh_guide", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		Guides.refresh();
	}
}