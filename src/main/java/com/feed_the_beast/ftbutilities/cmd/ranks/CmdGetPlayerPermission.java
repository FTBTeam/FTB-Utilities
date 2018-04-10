package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.FTBLibCommon;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

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
		RankConfigValueInfo info = FTBLibCommon.RANK_CONFIGS_MIRROR.get(args[1]);

		if (info != null)
		{
			ConfigValue value = Ranks.INSTANCE.getRank(player).getConfig(args[1]);
			FTBUtilitiesLang.PERM_FOR.sendMessage(sender, args[1], player.getName(), value.toString());
		}
		else
		{
			boolean perm = player.isOnline() ? PermissionAPI.hasPermission(player.getPlayer(), args[1]) : PermissionAPI.hasPermission(player.getProfile(), args[1], null);
			FTBUtilitiesLang.PERM_FOR.sendMessage(sender, args[1], player.getName(), Boolean.toString(perm));
		}
	}
}