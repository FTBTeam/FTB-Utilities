package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbl.lib.util.text_components.TextComponentCountdown;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.net.MessageServerInfo;
import com.feed_the_beast.ftbu.ranks.CmdOverride;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import com.feed_the_beast.ftbu.util.backups.Backups;
import com.google.gson.JsonElement;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.command.CommandTreeBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServerInfoPage
{
	private static JsonElement serverGuide = null;
	private static final Map<UUID, MessageServerInfo.CommandInfo> COMMAND_CACHE = new HashMap<>();

	public static void reloadCachedInfo()
	{
		serverGuide = null;
		COMMAND_CACHE.clear();
	}

	public static JsonElement getServerGuide()
	{
		if (serverGuide == null)
		{
			serverGuide = JsonUtils.fromJson(new File(CommonUtils.folderLocal, "ftbutilities/server_guide.json"));
		}

		return serverGuide;
	}

	public static MessageServerInfo.CommandInfo getCommands(EntityPlayerMP player)
	{
		MessageServerInfo.CommandInfo info = COMMAND_CACHE.get(player.getUniqueID());

		if (info == null)
		{
			info = new MessageServerInfo.CommandInfo("", Collections.emptyList(), new ArrayList<>());

			for (ICommand command : ServerUtils.getAllCommands(player.mcServer, player))
			{
				try
				{
					addCommandUsage(player, info, command);
				}
				catch (Exception ex1)
				{
				}
			}

			COMMAND_CACHE.put(player.getUniqueID(), info);
		}

		return info;
	}

	public static List<ITextComponent> getMainPage(EntityPlayerMP player, long now)
	{
		List<ITextComponent> list = new ArrayList<>();

		if (FTBUUniverseData.shutdownTime > 0L)
		{
			list.add(FTBULang.TIMER_SHUTDOWN.textComponent(new TextComponentCountdown(FTBUUniverseData.shutdownTime)));
		}

		if (FTBUConfig.backups.enabled)
		{
			list.add(FTBULang.TIMER_BACKUP.textComponent(new TextComponentCountdown(Backups.INSTANCE.nextBackup)));
		}

		if (FTBUConfig.server_info.difficulty)
		{
			list.add(FTBLibLang.DIFFICULTY.textComponent(StringUtils.firstUppercase(player.world.getDifficulty().toString().toLowerCase())));
		}

		if (FTBUConfig.server_info.motd && !FTBUConfig.login.getMOTD().isEmpty())
		{
			list.add(null);
			list.addAll(FTBUConfig.login.getMOTD());
		}

		return list;
	}

	private static void addCommandUsage(ICommandSender sender, MessageServerInfo.CommandInfo parent, ICommand command)
	{
		Collection<ITextComponent> info = new ArrayList<>();

		if (!command.getAliases().isEmpty())
		{
			info.add(new TextComponentString(command.getName()));

			for (String s : command.getAliases())
			{
				info.add(new TextComponentString(s));
			}

			info.add(null);
		}

		for (String line : command.getUsage(sender).split("\n"))
		{
			if (line.indexOf('%') != -1 || line.indexOf('/') != -1)
			{
				info.add(FTBLibLang.COMMAND_USAGE.textComponent(line));
			}
			else
			{
				info.add(FTBLibLang.COMMAND_USAGE.textComponent(new TextComponentTranslation(line)));
			}
		}

		MessageServerInfo.CommandInfo cmd = new MessageServerInfo.CommandInfo(command.getName(), info, new ArrayList<>());

		CommandTreeBase treeCommand = null;

		if (command instanceof CommandTreeBase)
		{
			treeCommand = (CommandTreeBase) command;
		}
		else if (command instanceof CmdOverride && ((CmdOverride) command).parent instanceof CommandTreeBase)
		{
			treeCommand = (CommandTreeBase) ((CmdOverride) command).parent;
		}

		if (treeCommand != null)
		{
			for (ICommand sub : treeCommand.getSubCommands())
			{
				addCommandUsage(sender, cmd, sub);
			}
		}

		parent.subcommands.add(new MessageServerInfo.CommandInfo(cmd.name, cmd.info.isEmpty() ? Collections.emptyList() : cmd.info, cmd.subcommands.isEmpty() ? Collections.emptyList() : cmd.subcommands));
	}
}