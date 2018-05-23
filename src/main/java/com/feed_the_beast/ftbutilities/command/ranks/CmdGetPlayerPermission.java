package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdGetPlayerPermission extends CmdBase
{
	public CmdGetPlayerPermission()
	{
		super("get_permission", Level.OP);
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("getp");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Ranks.INSTANCE == null ? FTBUtilitiesPermissionHandler.INSTANCE.getRegisteredNodes() : Ranks.INSTANCE.getPermissionNodes());
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
		checkArgs(sender, args, 2);
		ForgePlayer player = getForgePlayer(sender, args[0]);
		Node node = Node.get(args[1]);
		RankConfigValueInfo info = RankConfigAPI.getHandler().getInfo(node);

		if (info != null)
		{
			sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.get_permission.text", args[1], player.getDisplayName(), player.getRankConfig(node).toString()));
		}
		else
		{
			sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.get_permission.text", args[1], player.getDisplayName(), Boolean.toString(player.hasPermission(args[1]))));
		}
	}
}