package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IUniverse;
import com.feed_the_beast.ftbl.api.events.ServerInfoEvent;
import com.feed_the_beast.ftbl.lib.client.DrawableItem;
import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.ranks.CmdOverride;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import com.feed_the_beast.ftbu.world.backups.Backups;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerInfoPage
{
	private static final GuidePage CACHED_PAGE = new GuidePage("server_info").setTitle(StringUtils.translation("sidebar_button.ftbu.server_info"));

	public static void reloadCachedInfo()
	{
		CACHED_PAGE.clear();
		JsonElement json = JsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/server_guide.json"));
		if (!json.isJsonNull())
		{
			CACHED_PAGE.addSub(new GuidePage("local_guide", null, json));
		}
	}

	public static GuidePage getPageForPlayer(EntityPlayer ep)
	{
		IUniverse universe = FTBLibAPI.API.getUniverse();
		Preconditions.checkNotNull(universe, "World can't be null!");
		IForgePlayer self = universe.getPlayer(ep);
		Preconditions.checkNotNull(self, "Player can't be null!");

		GuidePage page = new GuidePage(CACHED_PAGE.getName());
		page.setTitle(CACHED_PAGE.getTitle());
		page.copyFrom(CACHED_PAGE);

		MinecraftServer server = ServerUtils.getServer();

		boolean isDedi = server.isDedicatedServer();
		boolean isOP = !isDedi || PermissionAPI.hasPermission(ep, FTBUPermissions.DISPLAY_ADMIN_INFO);
		FTBUUniverseData ftbuUniverseData = FTBUUniverseData.get();

		List<IForgePlayer> players = new ArrayList<>();
		players.addAll(universe.getPlayers());

		if (FTBUConfigGeneral.AUTO_RESTART.getBoolean())
		{
			page.println(FTBULang.TIMER_RESTART.textComponent(StringUtils.getTimeString(ftbuUniverseData.restartMillis - System.currentTimeMillis())));
		}

		if (FTBUConfigBackups.ENABLED.getBoolean())
		{
			page.println(FTBULang.TIMER_BACKUP.textComponent(StringUtils.getTimeString(Backups.INSTANCE.nextBackup - System.currentTimeMillis())));
		}

		if (FTBUConfigGeneral.SERVER_INFO_DIFFICULTY.getBoolean())
		{
			page.println(FTBLibLang.DIFFICULTY.textComponent(StringUtils.firstUppercase(ep.world.getDifficulty().toString().toLowerCase())));
		}

		if (FTBUConfigGeneral.SERVER_INFO_MODE.getBoolean())
		{
			page.println(FTBLibLang.MODE_CURRENT.textComponent(StringUtils.firstUppercase(FTBLibAPI.API.getServerData().getPackMode().getName())));
		}

		if (FTBUConfigGeneral.SERVER_INFO_ADMIN_QUICK_ACCESS.getBoolean())
		{
			//FIXME: SERVER_INFO_ADMIN_QUICK_ACCESS
		}

		ITextComponent leaderboardsTitle = StringUtils.text("Leaderboards");
		leaderboardsTitle.getStyle().setColor(TextFormatting.RED);
		GuidePage page1 = page.getSub("leaderboards").setTitle(leaderboardsTitle);
		page1.setIcon(new DrawableItem(new ItemStack(Items.SIGN)));
		page1.println("1.12: Work in progress!");

		/*
		for (Leaderboard leaderboard : FTBUCommon.LEADERBOARDS)
		{
			GuidePage thisTop = page1.getSub(leaderboard.stat.statId).setTitle(leaderboard.name);
			thisTop.setIcon(leaderboard.icon);
			players.sort(new InvertedComparator<>(leaderboard.comparator));

			int size = Math.min(players.size(), FTBUConfigGeneral.MAX_LEADERBOARD_SIZE.getInt());

			for (int j = 0; j < size; j++)
			{
				IForgePlayer p = players.get(j);
				Object data = leaderboard.data.getData(p);

				if (data == null)
				{
					data = "[null]";
				}

				StringBuilder sb = new StringBuilder();
				sb.append('[');
				sb.append(j + 1);
				sb.append(']');
				sb.append(' ');
				sb.append(p.getName());
				sb.append(':');
				sb.append(' ');
				if (!(data instanceof ITextComponent))
				{
					sb.append(data);
				}

				ITextComponent c = new TextComponentString(sb.toString());
				if (p == self)
				{
					c.getStyle().setColor(TextFormatting.DARK_GREEN);
				}
				else if (j < 3)
				{
					c.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
				}
				if (data instanceof ITextComponent)
				{
					c.appendSibling(ServerUtils.getChatComponent(data));
				}

				thisTop.println(c);
			}
		}
		*/

		new ServerInfoEvent(page, self, isOP).post();

		page1 = page.getSub("commands").setTitle(FTBLibLang.COMMANDS.textComponent());
		page1.setIcon(new DrawableItem(new ItemStack(Blocks.COMMAND_BLOCK)));

		try
		{
			for (ICommand c : ServerUtils.getAllCommands(server, ep))
			{
				try
				{
					addCommandUsage(ep, page1.getSub(c.getName()), 0, c);
				}
				catch (Exception ex1)
				{
					ITextComponent cc = StringUtils.text('/' + c.getName());
					cc.getStyle().setColor(TextFormatting.DARK_RED);
					page1.getSub('/' + c.getName()).setTitle(cc).println("Errored");

					if (LMUtils.DEV_ENV)
					{
						ex1.printStackTrace();
					}
				}
			}

			page1.sort(true);
		}
		catch (Exception ex)
		{
			page1.println("Failed to load commands");
		}

		if (PermissionAPI.hasPermission(ep, FTBUPermissions.DISPLAY_PERMISSIONS))
		{
			page.addSub(Ranks.INFO_PAGE);
		}

		page.cleanup();
		page.sort(false);
		return page;
	}

	private static void addCommandUsage(ICommandSender sender, GuidePage page, int level, ICommand c)
	{
		page.println('/' + c.getName());

		for (String s : c.getAliases())
		{
			page.println('/' + s);
		}

		page.println(null);

		for (String s : c.getUsage(sender).split("\n"))
		{
			if (s.indexOf('%') != -1 || s.indexOf('/') != -1)
			{
				page.println(StringUtils.translation("commands.generic.usage", s));
			}
			else
			{
				page.println(StringUtils.translation("commands.generic.usage", StringUtils.translation(s)));
			}
		}

		CommandTreeBase treeCommand = null;

		if (c instanceof CommandTreeBase)
		{
			treeCommand = (CommandTreeBase) c;
		}
		else if (c instanceof CmdOverride && ((CmdOverride) c).parent instanceof CommandTreeBase)
		{
			treeCommand = (CommandTreeBase) ((CmdOverride) c).parent;
		}

		if (treeCommand != null)
		{
			for (ICommand command : treeCommand.getSubCommands())
			{
				addCommandUsage(sender, page.getSub(command.getName()), level + 1, command);
			}
		}
	}
}