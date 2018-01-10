package com.feed_the_beast.ftbutilities.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftbutilities.FTBULang;
import com.feed_the_beast.ftbutilities.FTBUPermissions;
import com.feed_the_beast.ftbutilities.util.FTBUPlayerData;
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
		ForgePlayer p = getForgePlayer(player);

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