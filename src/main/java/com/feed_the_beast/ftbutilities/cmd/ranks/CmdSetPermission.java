package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 3);
		Rank rank = Ranks.INSTANCE.getRank(args[0], null);

		if (rank == null)
		{
			throw FTBUtilitiesLang.RANK_NOT_FOUND.commandError(args[0]);
		}

		Node node = Node.get(args[1]);

		if (args[2].equals("null"))
		{
			rank.permissions.remove(node);
			rank.cachedPermissions.remove(node);
			rank.cachedConfig.remove(node);
		}
		else if (args[2].equals("true") || args[2].equals("false"))
		{
			rank.permissions.put(node, new JsonPrimitive(args[2].equals("true")));
			rank.cachedPermissions.remove(node);
		}
		else
		{
			JsonElement element = DataReader.get(StringUtils.joinSpaceUntilEnd(2, args)).safeJson();

			if (element.isJsonObject())
			{
				throw FTBLibLang.FEATURE_DISABLED.commandError();
			}
			else if (!element.isJsonNull())
			{
				rank.permissions.put(node, element);
				rank.cachedConfig.remove(node);
			}
		}
	}
}