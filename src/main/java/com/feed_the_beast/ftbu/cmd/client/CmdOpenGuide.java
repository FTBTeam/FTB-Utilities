package com.feed_the_beast.ftbu.cmd.client;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.gui.guide.Guides;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdOpenGuide extends CmdBase
{
	public CmdOpenGuide()
	{
		super("open_guide", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		Guides.pageToOpen = args.length >= 1 ? args[0] : "";
		ClientUtils.MC.addScheduledTask(Guides.OPEN_GUI);
	}
}