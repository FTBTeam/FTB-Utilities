package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class CmdInfo extends CmdBase
{
	public CmdInfo()
	{
		super("info", Level.ALL);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return Ranks.isActive() ? getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.getRankNames(false)) : Collections.emptyList();
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!Ranks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		checkArgs(sender, args, 1);
		Rank rank = Ranks.INSTANCE.getRank(server, sender, args[0]);

		sender.sendMessage(new TextComponentString(""));
		ITextComponent id = new TextComponentString("[" + rank.getId() + (rank.comment.isEmpty() ? "]" : ("] - " + rank.comment)));
		id.getStyle().setColor(TextFormatting.YELLOW);
		id.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, rank.getDisplayName()));
		sender.sendMessage(id);

		Set<Rank> parents = rank.getParents();

		if (!parents.isEmpty())
		{
			ITextComponent t = new TextComponentString("");
			t.appendSibling(StringUtils.color(new TextComponentString(Rank.NODE_PARENT), TextFormatting.GOLD));
			t.appendText(": ");

			boolean first = true;

			for (Rank r : parents)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					t.appendText(", ");
				}

				ITextComponent t1 = new TextComponentString(r.getId());
				t1.getStyle().setColor(TextFormatting.AQUA);

				if (!r.comment.isEmpty())
				{
					t1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(r.comment)));
				}

				t1.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ranks info " + r.getId()));
				t.appendSibling(t1);
			}

			sender.sendMessage(t);
		}

		for (Rank.Entry entry : rank.permissions.values())
		{
			if (entry.node.equals(Rank.NODE_PARENT))
			{
				continue;
			}

			ITextComponent t = new TextComponentString("");
			t.appendSibling(StringUtils.color(new TextComponentString(entry.node), TextFormatting.GOLD));
			t.appendText(": ");
			t.appendSibling(StringUtils.color(new TextComponentString(entry.value), TextFormatting.BLUE));

			if (!entry.comment.isEmpty())
			{
				t.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(entry.comment)));
			}

			t.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ranks set_permission " + rank.getId() + " " + entry.node + " " + entry.value));
			sender.sendMessage(t);
		}
	}
}