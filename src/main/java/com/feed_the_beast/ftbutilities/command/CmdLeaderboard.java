package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
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
import java.util.ArrayList;
import java.util.List;

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
			return getListOfStringsMatchingLastWord(args, FTBUtilitiesCommon.LEADERBOARDS.keySet());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 0)
		{
			ITextComponent component = new TextComponentString("");
			component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, StringUtils.color(FTBLib.lang(sender, "click_here"), TextFormatting.GOLD)));
			boolean first = true;

			for (Leaderboard leaderboard : FTBUtilitiesCommon.LEADERBOARDS.values())
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
				component1.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/leaderboards " + leaderboard.id));
				component.appendSibling(component1);
			}

			sender.sendMessage(component);
		}
		else if (FTBUtilitiesCommon.LEADERBOARDS.get(new ResourceLocation(args[0])) != null)
		{
			Leaderboard leaderboard = FTBUtilitiesCommon.LEADERBOARDS.get(new ResourceLocation(args[0]));
			sender.sendMessage(leaderboard.getTitle().createCopy().appendText(":"));

			ForgePlayer p0 = sender instanceof EntityPlayerMP ? Universe.get().getPlayer(sender) : null;
			List<ForgePlayer> players = new ArrayList<>(Universe.get().getPlayers());
			players.sort(leaderboard.getComparator());

			for (int i = 0; i < players.size(); i++)
			{
				ForgePlayer p = players.get(i);
				ITextComponent component = new TextComponentString("#" + StringUtils.add0s(i + 1, players.size()) + " ").appendSibling(p.getDisplayName()).appendText(": ");
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