package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.TeleportLog;
import com.feed_the_beast.ftbutilities.data.TeleportType;
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
		ForgePlayer p = CommandUtils.getForgePlayer(player);

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(p);

		TeleportLog lastTeleportLog = data.getLastTeleportLog();

		if (lastTeleportLog == null)
		{
			throw FTBUtilities.error(sender, "ftbutilities.lang.warps.no_dp");
		}

		data.checkTeleportCooldown(sender, FTBUtilitiesPlayerData.Timer.BACK);

		FTBUtilitiesPlayerData.Timer.BACK.teleport(player, playerMP -> lastTeleportLog.teleporter(), universe ->
		{
			if (!PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.INFINITE_BACK_USAGE))
			{
				for (TeleportType t : TeleportType.values())
				{
					data.clearLastTeleport(t);
				}
			}
		});
	}
}