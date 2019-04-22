package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
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
		FTBUtilitiesPlayerData.Timer.RTP.teleport(player, playerMP -> findBlockPos(playerMP.server.getWorld(FTBUtilitiesConfig.world.spawn_dimension), player, 0), null);
	}

	private TeleporterDimPos findBlockPos(World world, EntityPlayerMP player, int depth)
	{
		if (++depth > FTBUtilitiesConfig.world.rtp_max_tries)
		{
			player.sendMessage(FTBUtilities.lang(player, "ftbutilities.lang.rtp.fail"));
			return TeleporterDimPos.of(player);
		}

		double dist = FTBUtilitiesConfig.world.rtp_min_distance + world.rand.nextDouble() * (FTBUtilitiesConfig.world.rtp_max_distance - FTBUtilitiesConfig.world.rtp_min_distance);
		double angle = world.rand.nextDouble() * Math.PI * 2D;

		int x = MathHelper.floor(Math.cos(angle) * dist);
		int y = 256;
		int z = MathHelper.floor(Math.sin(angle) * dist);

		if (!isInsideWorldBorder(world, x, y, z))
		{
			return findBlockPos(world, player, depth);
		}

		if (ClaimedChunks.instance != null && ClaimedChunks.instance.getChunk(new ChunkDimPos(x >> 4, z >> 4, world.provider.getDimension())) != null)
		{
			return findBlockPos(world, player, depth);
		}

		//TODO: Find a better way to check for biome without loading the chunk
		Biome biome = world.getBiome(new BlockPos(x, y, z));
		if (biome.getRegistryName().getPath().contains("ocean"))
		{
			return findBlockPos(world, player, depth);
		}

		Chunk chunk = world.getChunk(x >> 4, z >> 4);

		while (y > 0)
		{
			y--;

			if (chunk.getBlockState(x, y, z).getMaterial() != Material.AIR)
			{
				return TeleporterDimPos.of(x + 0.5D, y + 2.5D, z + 0.5D, world.provider.getDimension());
			}
		}

		return findBlockPos(world, player, depth);
	}

	private boolean isInsideWorldBorder(World world, double x, double y, double z)
	{
		WorldBorder border = world.getWorldBorder();
		return border.contains(new BlockPos(x, y, z));
	}

}