package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import com.google.gson.JsonElement;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdSetPermission extends CmdBase
{
	public static final List<String> PERM_VARIANTS = Arrays.asList("true", "false", "null");

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
			return Ranks.INSTANCE == null ? Collections.emptyList() : getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.getRankNames());
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, FTBUtilitiesPermissionHandler.INSTANCE.getRegisteredNodes());
		}
		else if (args.length == 3)
		{
			Node node = Node.get(args[1]);

			RankConfigValueInfo info = RankConfigAPI.getHandler().getInfo(node);

			if (info != null && !info.defaultValue.isNull())
			{
				return getListOfStringsMatchingLastWord(args, info.defaultValue.getVariants());
			}

			return getListOfStringsMatchingLastWord(args, PERM_VARIANTS);
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (Ranks.INSTANCE == null)
		{
			throw new CommandException("feature_disabled_server");
		}

		checkArgs(sender, args, 3);
		Rank rank = Ranks.INSTANCE.getRank(args[0]);

		if (rank == null)
		{
			throw new CommandException("commands.ftb.ranks.not_found", args[0]);
		}

		Node node = Node.get(args[1]);
		JsonElement element = DataReader.get(StringUtils.joinSpaceUntilEnd(2, args)).safeJson();

		if (element.isJsonObject())
		{
			throw new CommandException("wip");
		}

		String set = rank.setPermission(node, element);

		if (set.isEmpty())
		{
			sender.sendMessage(FTBUtilities.lang(sender, "commands.ftb.ranks.set_permission.nothing_changed"));
		}
		else
		{
			Ranks.INSTANCE.saveAndUpdate(server, node);
			ITextComponent nodeText = new TextComponentString(node.toString());
			nodeText.getStyle().setColor(TextFormatting.GOLD);

			ITextComponent rankText = new TextComponentString(rank.getName());
			rankText.getStyle().setColor(TextFormatting.DARK_GREEN);

			ITextComponent setText = new TextComponentString(set);

			if (set.equals("true"))
			{
				setText.getStyle().setColor(TextFormatting.GREEN);
			}
			else if (set.equals("false"))
			{
				setText.getStyle().setColor(TextFormatting.RED);
			}
			else if (set.equals("none"))
			{
				setText.getStyle().setColor(TextFormatting.DARK_GRAY);
			}
			else
			{
				setText.getStyle().setColor(TextFormatting.BLUE);
			}

			sender.sendMessage(FTBUtilities.lang(sender, "commands.ftb.ranks.set_permission.set", nodeText, rankText, setText));
		}
	}
}