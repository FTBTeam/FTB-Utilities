package com.feed_the_beast.ftbutilities.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CmdSpawn extends CmdBase
{
	public CmdSpawn()
	{
		super("spawn", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(getForgePlayer(player));

		long cooldown = data.getTeleportCooldown(FTBUtilitiesPlayerData.Timer.SPAWN);

		if (cooldown > 0)
		{
			throw new CommandException("cant_use_now_cooldown", StringUtils.getTimeStringTicks(cooldown));
		}

		World w = server.getWorld(0);
		BlockPos spawnpoint = w.getSpawnPoint();

		while (w.getBlockState(spawnpoint).isFullCube())
		{
			spawnpoint = spawnpoint.up(2);
		}

		FTBUtilitiesPlayerData.Timer.SPAWN.teleport(player, new BlockDimPos(spawnpoint, 0), null);
	}
}