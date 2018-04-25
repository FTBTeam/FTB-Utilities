package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.FTBLibCommon;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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
		RankConfigValueInfo info = FTBLibCommon.RANK_CONFIGS_MIRROR.get(node);

		if (info != null)
		{
			FTBUtilitiesLang.PERM_FOR.sendMessage(sender, args[1], player.getName(), RankConfigAPI.get(player, Node.get(args[1])).toString());
		}
		else
		{
			FTBUtilitiesLang.PERM_FOR.sendMessage(sender, args[1], player.getName(), Boolean.toString(player.hasPermission(args[1])));
		}
	}
}