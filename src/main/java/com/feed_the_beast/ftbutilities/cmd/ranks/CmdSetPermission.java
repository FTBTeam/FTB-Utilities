package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import com.google.gson.JsonElement;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdSetPermission extends CmdBase
{
	public CmdSetPermission()
	{
		super("set_permission", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.getRankNames());
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

			return getListOfStringsMatchingLastWord(args, ConfigBoolean.VARIANTS);
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 3);
		Rank rank = Ranks.INSTANCE.getRank(args[0], null);

		if (rank == null)
		{
			throw new CommandException("ftbutilities.lang.rank.not_found", args[0]);
		}

		Node node = Node.get(args[1]);

		switch (args[2])
		{
			case "null":
				if (rank.permissions.remove(node) != null)
				{
					Ranks.INSTANCE.saveAndUpdate(server, node);
				}
				break;
			case "true":
			case "false":
				JsonElement json = args[2].equals("true") ? JsonUtils.JSON_TRUE : JsonUtils.JSON_FALSE;

				if (!JsonUtils.nonnull(rank.permissions.put(node, json)).equals(json))
				{
					Ranks.INSTANCE.saveAndUpdate(server, node);
					sender.sendMessage(new TextComponentString("Changed permission '" + node + "' for '" + rank.getName() + "' to '" + args[2].equals("true") + "'")); //LANG
				}
				else
				{
					sender.sendMessage(new TextComponentString("Nothing changed!")); //LANG
				}
				break;
			default:
				JsonElement element = DataReader.get(StringUtils.joinSpaceUntilEnd(2, args)).safeJson();

				if (element.isJsonObject())
				{
					throw new CommandException("wip");
				}
				else if (!element.isJsonNull())
				{
					if (!JsonUtils.nonnull(rank.permissions.put(node, element)).equals(element))
					{
						Ranks.INSTANCE.saveAndUpdate(server, node);
						sender.sendMessage(new TextComponentString("Changed permission config '" + node + "' for '" + rank.getName() + "' to '" + element + "'")); //LANG
					}
					else
					{
						sender.sendMessage(new TextComponentString("Nothing changed!")); //LANG
					}
				}
		}
	}
}