package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
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

		ServerUtils.teleportPlayer(player, new BlockDimPos(spawnpoint, 0));
		FTBULang.WARP_SPAWN.sendMessage(sender);
	}
}