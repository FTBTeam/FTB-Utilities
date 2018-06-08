package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdShutdownTime extends CmdBase
{
	public CmdShutdownTime()
	{
		super("shutdown_time", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (FTBUtilitiesUniverseData.shutdownTime > 0L)
		{
			sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.timer.shutdown", StringUtils.getTimeString(FTBUtilitiesUniverseData.shutdownTime - System.currentTimeMillis())));
		}
		else
		{
			throw FTBLib.errorFeatureDisabledServer(sender);
		}
	}
}