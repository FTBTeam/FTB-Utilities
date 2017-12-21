package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftbu.FTBULang;
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
		World w = server.getEntityWorld();
		BlockPos spawnpoint = w.getSpawnPoint();

		while (w.getBlockState(spawnpoint).isFullCube())
		{
			spawnpoint = spawnpoint.up(2);
		}

		ServerUtils.teleportEntity(player, new BlockDimPos(spawnpoint, 0));
		FTBULang.WARP_SPAWN.sendMessage(sender);
	}
}