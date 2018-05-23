package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
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

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(p);

		if (data.getLastDeath() == null)
		{
			throw new CommandException("ftbutilities.lang.warps.no_dp");
		}

		long cooldown = data.getTeleportCooldown(FTBUtilitiesPlayerData.Timer.BACK);

		if (cooldown > 0)
		{
			throw new CommandException("cant_use_now_cooldown", StringUtils.getTimeStringTicks(cooldown));
		}

		FTBUtilitiesPlayerData.Timer.BACK.teleport(player, data.getLastDeath(), universe ->
		{
			if (!PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.INFINITE_BACK_USAGE))
			{
				data.setLastDeath(null);
			}
		});
	}
}