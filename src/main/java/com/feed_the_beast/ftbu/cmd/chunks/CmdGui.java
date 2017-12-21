package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.net.MessageOpenClaimedChunksGui;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdGui extends CmdBase
{
	public CmdGui()
	{
		super("gui", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		new MessageOpenClaimedChunksGui().sendTo(getCommandSenderAsPlayer(sender));
	}
}