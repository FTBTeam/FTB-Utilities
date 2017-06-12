package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdEdit extends CmdBase
{
	public CmdEdit()
	{
		super("edit", Level.OP);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		throw FTBLibLang.FEATURE_DISABLED.commandError();
	}
}