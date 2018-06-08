package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.backups.Backup;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

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
			if (Backups.INSTANCE.run(server, sender, args.length == 0 ? "" : args[0]))
			{
				for (EntityPlayerMP player : server.getPlayerList().getPlayers())
				{
					player.sendMessage(FTBUtilities.lang(player, "ftbutilities.lang.backup.manual_launch", sender.getName()));
				}
			}
			else
			{
				sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.backup.already_running"));
			}
		}
	}

	public static class CmdGetSize extends CmdBase
	{
		public CmdGetSize()
		{
			super("size", Level.OP);
		}

		@Override
		public List<String> getAliases()
		{
			return Collections.singletonList("getsize");
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			String sizeW = FileUtils.getSizeString(server.getWorld(0).getSaveHandler().getWorldDirectory());

			long totalSize = 0L;

			for (Backup backup : Backups.INSTANCE.backups)
			{
				totalSize += backup.size;
			}

			String sizeT = FileUtils.getSizeString(totalSize);
			sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.backup.size", sizeW, sizeT));
		}
	}

	public static class CmdTime extends CmdBase
	{
		public CmdTime()
		{
			super("time", Level.ALL);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.timer.backup", StringUtils.getTimeString(Backups.INSTANCE.nextBackup - System.currentTimeMillis())));
		}
	}

	public CmdBackup()
	{
		super("backup");
		addSubcommand(new CmdStart());
		addSubcommand(new CmdGetSize());
		addSubcommand(new CmdTime());
		addSubcommand(new CmdTreeHelp(this));
	}
}