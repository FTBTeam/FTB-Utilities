package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.util.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class CmdHome extends CmdBase
{
	public CmdHome()
	{
		super("home", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(FTBLibAPI.API.getUniverse().getPlayer(sender)).homes.list());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
		FTBUPlayerData data = FTBUPlayerData.get(getForgePlayer(ep));

		if (args.length == 0)
		{
			args = new String[] {"home"};
		}

		if (args[0].equals("list"))
		{
			Collection<String> list = data.homes.list();
			sender.sendMessage(new TextComponentString(list.size() + " / " + FTBUtilitiesAPI.API.getRankConfig(ep, FTBUPermissions.HOMES_MAX).getInt() + ": "));
			if (!list.isEmpty())
			{
				sender.sendMessage(new TextComponentString(StringUtils.strip(list)));
			}
			return;
		}

		BlockDimPos pos = data.homes.get(args[0]);

		if (pos == null)
		{
			throw FTBULang.HOME_NOT_SET.commandError(args[0]);
		}
		else if (ep.dimension != pos.dim && !PermissionAPI.hasPermission(ep, FTBUPermissions.HOMES_CROSS_DIM))
		{
			throw FTBULang.HOME_CROSS_DIM.commandError();
		}

		ServerUtils.teleportPlayer(ep, pos);
		FTBULang.WARP_TP.sendMessage(sender, args[0]);
	}
}