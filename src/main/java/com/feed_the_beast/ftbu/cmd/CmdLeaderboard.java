package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.api.Leaderboard;
import com.feed_the_beast.ftbu.net.MessageSendLeaderboard;
import com.feed_the_beast.ftbu.net.MessageSendLeaderboardList;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class CmdLeaderboard extends CmdBase
{
	public CmdLeaderboard()
	{
		super("leaderboards", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUCommon.LEADERBOARDS.getKeys());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 0)
		{
			ITextComponent component = new TextComponentString("");
			component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBLibLang.CLICK_HERE.textComponent(sender)));
			boolean first = true;

			for (Leaderboard leaderboard : FTBUCommon.LEADERBOARDS)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					component.appendText(", ");
				}

				ITextComponent component1 = leaderboard.getTitle().createCopy();
				component1.getStyle().setColor(TextFormatting.GOLD);
				component1.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftb leaderboards gui " + leaderboard.getRegistryName()));
				component.appendSibling(component1);
			}

			sender.sendMessage(component);
		}
		else if (args[0].equals("gui"))
		{
			EntityPlayerMP player = getCommandSenderAsPlayer(sender);

			if (args.length == 1)
			{
				Map<ResourceLocation, ITextComponent> map = new LinkedHashMap<>();

				for (Leaderboard leaderboard : FTBUCommon.LEADERBOARDS)
				{
					map.put(leaderboard.getRegistryName(), leaderboard.getTitle());
				}

				new MessageSendLeaderboardList(map).sendTo(player);
				return;
			}

			Leaderboard leaderboard = FTBUCommon.LEADERBOARDS.getValue(new ResourceLocation(args[1]));

			if (leaderboard != null)
			{
				new MessageSendLeaderboard(player, leaderboard).sendTo(player);
			}
		}
		else if (FTBUCommon.LEADERBOARDS.getValue(new ResourceLocation(args[0])) != null)
		{
			Leaderboard leaderboard = FTBUCommon.LEADERBOARDS.getValue(new ResourceLocation(args[0]));
			sender.sendMessage(leaderboard.getTitle().createCopy().appendText(":"));

			IForgePlayer p0 = FTBLibAPI.API.getUniverse().getPlayer(sender);
			List<IForgePlayer> players = FTBLibAPI.API.getUniverse().getRealPlayers();
			players.sort(leaderboard.getComparator());

			for (int i = 0; i < players.size(); i++)
			{
				IForgePlayer p = players.get(i);
				ITextComponent component = new TextComponentString("#" + StringUtils.add0s(i + 1, players.size()) + " " + p.getName() + ": ");
				component.appendSibling(leaderboard.createValue(p));

				if (p == p0)
				{
					component.getStyle().setColor(TextFormatting.DARK_GREEN);
				}
				else if (!leaderboard.hasValidValue(p))
				{
					component.getStyle().setColor(TextFormatting.DARK_GRAY);
				}
				else if (i < 3)
				{
					component.getStyle().setColor(TextFormatting.GOLD);
				}

				sender.sendMessage(component);
			}
		}
		else
		{
			sender.sendMessage(new TextComponentString("Invalid ID!")); //LANG
		}
	}
}