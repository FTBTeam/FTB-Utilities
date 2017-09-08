package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.FileUtils;
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
		FileUtils.newFile(new File(CommonUtils.folderMinecraft, "autostart.stamp"));
		server.initiateShutdown();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		shutdown(server);
	}
}