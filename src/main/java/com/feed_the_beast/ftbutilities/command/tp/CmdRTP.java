package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class CmdRTP extends CmdBase
{
	public CmdRTP()
	{
		super("rtp", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(CommandUtils.getForgePlayer(player));
		data.checkTeleportCooldown(sender, FTBUtilitiesPlayerData.Timer.RTP);

		FTBUtilitiesPlayerData.Timer.RTP.teleport(player, playerMP ->
		{
			World w = playerMP.server.getWorld(FTBUtilitiesConfig.world.spawn_dimension);

			double dist = FTBUtilitiesConfig.world.rtp_min_distance + w.rand.nextDouble() * (FTBUtilitiesConfig.world.rtp_max_distance - FTBUtilitiesConfig.world.rtp_min_distance);
			double angle = w.rand.nextDouble() * Math.PI * 2D;

			BlockPos spawnpoint = new BlockPos(Math.cos(angle) * dist, 256, Math.sin(angle) * dist);

			Chunk chunk = w.getChunk(spawnpoint);

			while (spawnpoint.getY() > 0)
			{
				spawnpoint = spawnpoint.down();

				if (chunk.getBlockState(spawnpoint).getMaterial() != Material.AIR)
				{
					spawnpoint.up();
					break;
				}
			}

			return new BlockDimPos(spawnpoint, FTBUtilitiesConfig.world.spawn_dimension).teleporter();
		}, null);
	}
}