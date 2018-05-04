package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdGetPlayerPermission extends CmdBase
{
	public CmdGetPlayerPermission()
	{
		super("get_player_permission", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, FTBUtilitiesPermissionHandler.INSTANCE.getRegisteredNodes());
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
			FTBUtilitiesLang.PERM_FOR.sendMessage(sender, args[1], player.getName(), player.getRankConfig(node).toString());
		}
		else
		{
			FTBUtilitiesLang.PERM_FOR.sendMessage(sender, args[1], player.getName(), Boolean.toString(player.hasPermission(args[1])));
		}
	}
}