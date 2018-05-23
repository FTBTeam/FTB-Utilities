package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeHelp;

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
			super("getsize", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			String sizeW = FileUtils.getSizeString(server.getWorld(0).getSaveHandler().getWorldDirectory());
			String sizeT = FileUtils.getSizeString(Backups.INSTANCE.backupsFolder);
			sender.sendMessage(FTBUtilities.lang(sender, "ftbutilities.lang.backup.size", sizeW, sizeT));
		}
	}

	public CmdBackup()
	{
		super("backup");
		addSubcommand(new CmdStart());
		addSubcommand(new CmdGetSize());
		addSubcommand(new CommandTreeHelp(this));
	}
}