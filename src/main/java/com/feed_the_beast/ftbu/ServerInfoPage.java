package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IUniverse;
import com.feed_the_beast.ftbl.lib.icon.ItemIcon;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.ServerInfoEvent;
import com.feed_the_beast.ftbu.gui.guide.GuidePage;
import com.feed_the_beast.ftbu.ranks.CmdOverride;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import com.feed_the_beast.ftbu.util.backups.Backups;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerInfoPage
{
	private static JsonElement serverGuide = null;
	private static final Map<UUID, JsonObject> COMMAND_CACHE = new HashMap<>();

	public static void reloadCachedInfo()
	{
		serverGuide = null;
		COMMAND_CACHE.clear();
	}

	public static JsonElement getServerGuide()
	{
		if (serverGuide == null)
		{
			serverGuide = JsonUtils.fromJson(new File(CommonUtils.folderLocal, "ftbu/server_guide.json"));
		}

		return serverGuide;
	}

	public static JsonObject getCommands(EntityPlayerMP player)
	{
		return new JsonObject();
	}

	public static JsonObject getPage(EntityPlayerMP player)
	{
		return new JsonObject();
	}

	public static GuidePage getPageForPlayer(EntityPlayerMP player)
	{
		IUniverse universe = FTBLibAPI.API.getUniverse();
		Preconditions.checkNotNull(universe, "World can't be null!");
		IForgePlayer self = universe.getPlayer(player);
		Preconditions.checkNotNull(self, "Player can't be null!");

		//FIXME
		GuidePage page = new GuidePage("server_info", null);

		MinecraftServer server = ServerUtils.getServer();

		boolean isDedi = server.isDedicatedServer();
		boolean isOP = !isDedi || PermissionAPI.hasPermission(player, FTBUPermissions.DISPLAY_ADMIN_INFO);

		//List<IForgePlayer> players = new ArrayList<>();
		//players.addAll(universe.getPlayers());
		long now = ServerUtils.getWorldTime();

		if (FTBUUniverseData.shutdownTime > 0L)
		{
			page.println(FTBULang.TIMER_SHUTDOWN.textComponent(StringUtils.getTimeStringTicks(FTBUUniverseData.shutdownTime - now)));
		}

		if (FTBUConfig.backups.enabled)
		{
			page.println(FTBULang.TIMER_BACKUP.textComponent(StringUtils.getTimeStringTicks(Backups.INSTANCE.nextBackup - now)));
		}

		if (FTBUConfig.server_info.difficulty)
		{
			page.println(FTBLibLang.DIFFICULTY.textComponent(StringUtils.firstUppercase(player.world.getDifficulty().toString().toLowerCase())));
		}

		if (FTBUConfig.server_info.motd && !FTBUConfig.login.getMOTD().isEmpty())
		{
			page.println(null);

			for (ITextComponent component : FTBUConfig.login.getMOTD())
			{
				page.println(component);
			}
		}

		//if (FTBUConfig.server_info.admin_quick_access)
		//{
		//FIXME: SERVER_INFO_ADMIN_QUICK_ACCESS
		//}

		IGuidePage page1 = page.getSub("leaderboards").setTitle(StringUtils.color(FTBULang.LEADERBOARDS.textComponent(), TextFormatting.RED));
		page1.setIcon(new ItemIcon(new ItemStack(Items.SIGN)));
		page1.println(new TextComponentString("1.12+: ").appendSibling(FTBLibLang.WIP.textComponent()));

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
		page1.setIcon(new ItemIcon(new ItemStack(Blocks.COMMAND_BLOCK)));

		try
		{
			for (ICommand c : ServerUtils.getAllCommands(server, player))
			{
				try
				{
					addCommandUsage(player, page1.getSub(c.getName()), 0, c);
				}
				catch (Exception ex1)
				{
					ITextComponent cc = new TextComponentString('/' + c.getName());
					cc.getStyle().setColor(TextFormatting.DARK_RED);
					page1.getSub('/' + c.getName()).setTitle(cc).println(FTBLibLang.ERROR.textComponent(ex1.toString()));

					if (CommonUtils.DEV_ENV)
					{
						ex1.printStackTrace();
					}
				}
			}

			page1.sort(true);
		}
		catch (Exception ex)
		{
			page1.println(FTBULang.GUIDE_COMMANDS_FAILED.textComponent());
		}

		if (PermissionAPI.hasPermission(player, FTBUPermissions.DISPLAY_PERMISSIONS))
		{
			page.addSub(Ranks.INFO_PAGE);
		}

		page.cleanup();
		page.sort(false);
		return page;
	}

	private static void addCommandUsage(ICommandSender sender, IGuidePage page, int level, ICommand c)
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
				page.println(FTBLibLang.COMMAND_USAGE.textComponent(s));
			}
			else
			{
				page.println(FTBLibLang.COMMAND_USAGE.textComponent(new TextComponentTranslation(s)));
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
				addCommandUsage(sender, page.getSub(command.getName()).setTitle(new TextComponentString(command.getName())), level + 1, command);
			}
		}
	}
}