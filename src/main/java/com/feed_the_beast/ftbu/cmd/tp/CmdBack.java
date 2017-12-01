package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.util.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public class CmdBack extends CmdBase
{
	public CmdBack()
	{
		super("back", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		IForgePlayer p = getForgePlayer(player);

		FTBUPlayerData data = FTBUPlayerData.get(p);

		if (data.lastDeath == null)
		{
			throw FTBULang.WARP_NO_DP.commandError();
		}

		ServerUtils.teleportEntity(player, data.lastDeath);

		if (!PermissionAPI.hasPermission(player, FTBUPermissions.INFINITE_BACK_USAGE))
		{
			data.lastDeath = null;
		}
	}
}