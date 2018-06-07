package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
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
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(CommandUtils.getForgePlayer(player));
		data.checkTeleportCooldown(sender, FTBUtilitiesPlayerData.Timer.SPAWN);
		World w = server.getWorld(FTBUtilitiesConfig.world.spawn_dimension);
		BlockPos spawnpoint = w.getSpawnPoint();

		while (w.getBlockState(spawnpoint).isFullCube())
		{
			spawnpoint = spawnpoint.up(2);
		}

		FTBUtilitiesPlayerData.Timer.SPAWN.teleport(player, new BlockDimPos(spawnpoint, FTBUtilitiesConfig.world.spawn_dimension), null);
	}
}