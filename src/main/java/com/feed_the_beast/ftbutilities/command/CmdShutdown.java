package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.io.File;

public class CmdShutdown extends CmdBase
{
	public CmdShutdown()
	{
		super("shutdown", Level.OP);
	}

	public static void shutdown(MinecraftServer server)
	{
		FileUtils.newFile(new File(server.getDataDirectory(), "autostart.stamp"));
		server.initiateShutdown();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		shutdown(server);
	}
}