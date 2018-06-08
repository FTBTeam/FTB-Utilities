package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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
			RankConfigValueInfo info = RankConfigAPI.getHandler().getInfo(Node.get(args[1]));

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!Ranks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		checkArgs(sender, args, 3);
		Rank rank = Ranks.INSTANCE.getRank(args[0]);

		if (rank.isNone())
		{
			throw FTBUtilities.error(sender, "commands.ranks.not_found", args[0]);
		}

		Node node = Node.get(args[1]);
		String json0 = StringUtils.joinSpaceUntilEnd(2, args);
		JsonElement element = json0.equals("none") ? JsonNull.INSTANCE : DataReader.get(json0).safeJson();

		if (element.isJsonObject())
		{
			throw FTBLib.error(sender, "wip");
		}

		if (!rank.setPermission(node, element))
		{
			sender.sendMessage(FTBLib.lang(sender, "nothing_changed"));
		}
		else
		{
			Ranks.INSTANCE.universe.clearCache();
			Ranks.INSTANCE.saveRanks();
			ITextComponent nodeText = new TextComponentString(node.toString());
			nodeText.getStyle().setColor(TextFormatting.GOLD);

			ITextComponent setText;

			if (JsonUtils.isNull(element))
			{
				setText = FTBUtilities.lang(sender, "commands.ranks.none");
				setText.getStyle().setColor(TextFormatting.DARK_GRAY);
			}
			else
			{
				String set = element.toString();
				setText = new TextComponentString(set);

				switch (set)
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