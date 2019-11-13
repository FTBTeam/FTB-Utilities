package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdSetPermission extends CmdBase
{
	public static final List<String> PERM_VARIANTS = Arrays.asList("true", "false", "none");

	public CmdSetPermission()
	{
		super("set_permission", Level.OP);
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("setp");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return Ranks.isActive() ? getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.getRankNames(false)) : Collections.emptyList();
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Ranks.isActive() ? Ranks.INSTANCE.getPermissionNodes() : FTBUtilitiesPermissionHandler.INSTANCE.getRegisteredNodes());
		}
		else if (args.length == 3)
		{
			RankConfigValueInfo info = RankConfigAPI.getHandler().getInfo(args[1]);

			if (info != null && !info.defaultValue.isNull())
			{
				List<String> list = new ArrayList<>(info.defaultValue.getVariants());
				list.add("none");
				return getListOfStringsMatchingLastWord(args, list);
			}

			return getListOfStringsMatchingLastWord(args, PERM_VARIANTS);
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!Ranks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		checkArgs(sender, args, 3);
		Rank rank = Ranks.INSTANCE.getRank(server, sender, args[0]);

		String node = args[1];
		String value0 = StringUtils.joinSpaceUntilEnd(2, args);
		String value = value0.equals("none") ? "" : value0;

		if (value.length() > 2 && value.startsWith("\"") && value.endsWith("\""))
		{
			value = value.substring(1, value.length() - 1);
		}

		if (rank.setPermission(node, value) == null)
		{
			sender.sendMessage(FTBLib.lang(sender, "nothing_changed"));
		}
		else
		{
			rank.ranks.save();
			ITextComponent nodeText = new TextComponentString(node);
			nodeText.getStyle().setColor(TextFormatting.GOLD);

			ITextComponent setText;

			if (value.isEmpty())
			{
				setText = FTBUtilities.lang(sender, "commands.ranks.none");
				setText.getStyle().setColor(TextFormatting.DARK_GRAY);
			}
			else
			{
				setText = new TextComponentString(value);

				switch (value)
				{
					case "true":
						setText.getStyle().setColor(TextFormatting.GREEN);
						break;
					case "false":
						setText.getStyle().setColor(TextFormatting.RED);
						break;
					default:
						setText.getStyle().setColor(TextFormatting.BLUE);
						break;
				}
			}

			sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.set_permission.set", nodeText, rank.getDisplayName(), setText));
		}
	}
}