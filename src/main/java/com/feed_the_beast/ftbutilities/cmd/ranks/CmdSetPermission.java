package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import com.google.gson.JsonElement;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.Event;

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

		if (args[2].equals("null"))
		{
			rank.permissions.remove(args[1]);
			rank.cachedPermissions.remove(args[1]);
			rank.config.remove(args[1]);
			rank.cachedConfig.remove(args[1]);
		}
		else if (args[2].equals("true") || args[2].equals("false"))
		{
			rank.permissions.put(args[1], args[2].equals("true") ? Event.Result.ALLOW : Event.Result.DENY);
			rank.cachedPermissions.remove(args[1]);
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
				//rank.config.put(args[1], );
				rank.cachedConfig.remove(args[1]);
			}
		}
	}
}