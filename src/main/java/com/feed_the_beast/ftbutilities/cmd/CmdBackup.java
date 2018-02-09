package com.feed_the_beast.ftbutilities.cmd;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.misc.BroadcastSender;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CmdBackup extends CmdTreeBase
{
	public static class CmdStart extends CmdBase
	{
		public CmdStart()
		{
			super("start", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			boolean b = Backups.INSTANCE.run(server, sender, args.length == 0 ? "" : args[0]);
			if (b)
			{
				FTBUtilitiesLang.BACKUP_MANUAL_LAUNCH.sendMessage(new BroadcastSender(server), sender.getName());

				if (!FTBUtilitiesConfig.backups.use_separate_thread)
				{
					Backups.INSTANCE.postBackup(server);
				}
			}
			else
			{
				FTBUtilitiesLang.BACKUP_ALREADY_RUNNING.sendMessage(sender);
			}
		}
	}

	public static class CmdStop extends CmdBase
	{
		public CmdStop()
		{
			super("stop", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			if (Backups.INSTANCE.thread != null)
			{
				Backups.INSTANCE.thread.interrupt();
				Backups.INSTANCE.thread = null;
				FTBUtilitiesLang.BACKUP_STOP.sendMessage(sender);
				return;
			}

			throw FTBUtilitiesLang.BACKUP_NOT_RUNNING.commandError();
		}
	}

	public static class CmdGetSize extends CmdBase
	{
		public CmdGetSize()
		{
			super("getsize", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			String sizeW = FileUtils.getSizeS(sender.getEntityWorld().getSaveHandler().getWorldDirectory());
			String sizeT = FileUtils.getSizeS(Backups.INSTANCE.backupsFolder);
			FTBUtilitiesLang.BACKUP_SIZE.sendMessage(sender, sizeW, sizeT);
		}
	}

	public CmdBackup()
	{
		super("backup");
		addSubcommand(new CmdStart());
		addSubcommand(new CmdStop());
		addSubcommand(new CmdGetSize());
	}
}