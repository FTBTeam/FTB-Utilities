package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.util.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdAdminHome extends CmdTreeBase
{
	public static class CmdTP extends CmdBase
	{
		public CmdTP()
		{
			super("tp", Level.OP);
		}

		@Override
		public boolean isUsernameIndex(String[] args, int i)
		{
			return i == 0;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
			checkArgs(args, 2, "<player> <home>");
			args[1] = args[1].toLowerCase();
			FTBUPlayerData data = FTBUPlayerData.get(getForgePlayer(args[0]));
			BlockDimPos pos = data.homes.get(args[1]);

			if (pos != null)
			{
				ServerUtils.teleportPlayer(ep, pos);
				FTBULang.WARP_TP.sendMessage(sender, args[1]);
			}

			throw FTBULang.HOME_NOT_SET.commandError(args[1]);
		}
	}

	public static class CmdList extends CmdBase
	{
		public CmdList()
		{
			super("list", Level.OP);
		}

		@Override
		public boolean isUsernameIndex(String[] args, int i)
		{
			return i == 0;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			checkArgs(args, 1, "<player>");
			FTBUPlayerData data = FTBUPlayerData.get(getForgePlayer(args[0]));
			sender.sendMessage(new TextComponentString(StringUtils.strip(data.homes.list())));
		}
	}

	public static class CmdRem extends CmdBase
	{
		public CmdRem()
		{
			super("remove", Level.OP);
		}

		@Override
		public boolean isUsernameIndex(String[] args, int i)
		{
			return i == 0;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			checkArgs(args, 2, "<player> <home>");
			FTBUPlayerData data = FTBUPlayerData.get(getForgePlayer(args[0]));
			args[1] = args[1].toLowerCase();
			BlockDimPos pos = data.homes.get(args[1]);

			if (pos != null && data.homes.set(args[1], null))
			{
				FTBULang.HOME_DEL.sendMessage(sender, args[1]);
			}

			throw FTBULang.HOME_NOT_SET.commandError(args[1]);
		}
	}

	public CmdAdminHome()
	{
		super("admin_home");
		addSubcommand(new CmdTP());
		addSubcommand(new CmdList());
		addSubcommand(new CmdRem());
	}
}