package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.internal.FTBLibNotifications;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api.events.ChunkModifiedEvent;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.FTBUChunkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class FTBUUniverseData
{
	public static long shutdownTime;
	public static long nextChunkloaderUpdate;
	public static final BlockDimPosStorage WARPS = new BlockDimPosStorage();

	public static boolean isInSpawn(ChunkDimPos pos)
	{
		MinecraftServer server = ServerUtils.getServer();

		if (pos.dim != 0 || (!server.isDedicatedServer() && !FTBUConfig.world.spawn_area_in_sp))
		{
			return false;
		}

		int radius = server.getSpawnProtectionSize();
		if (radius <= 0)
		{
			return false;
		}

		BlockPos c = server.getEntityWorld().getSpawnPoint();
		int minX = MathUtils.chunk(c.getX() - radius);
		int minZ = MathUtils.chunk(c.getZ() - radius);
		int maxX = MathUtils.chunk(c.getX() + radius);
		int maxZ = MathUtils.chunk(c.getZ() + radius);
		return pos.posX >= minX && pos.posX <= maxX && pos.posZ >= minZ && pos.posZ <= maxZ;
	}

	public static boolean isInSpawnD(int dim, double x, double z)
	{
		return dim == 0 && isInSpawn(new ChunkDimPos(MathUtils.chunk(x), MathUtils.chunk(z), dim));
	}

	public static boolean claimChunk(IForgePlayer player, ChunkDimPos pos)
	{
		if (!FTBUConfig.world.chunk_claiming || !FTBUPermissions.allowDimension(player.getProfile(), pos.dim))
		{
			return false;
		}

		if (player.getTeam() == null)
		{
			if (player.isOnline())
			{
				FTBLibNotifications.NO_TEAM.send(player.getPlayer());
			}

			return false;
		}

		int max = FTBUtilitiesAPI.API.getRankConfig(player.getProfile(), FTBUPermissions.CLAIMS_MAX_CHUNKS).getInt();
		if (max == 0)
		{
			return false;
		}

		if (ClaimedChunkStorage.INSTANCE.getChunks(player).size() >= max)
		{
			return false;
		}

		IForgePlayer chunkOwner = ClaimedChunkStorage.INSTANCE.getChunkOwner(pos);

		if (chunkOwner != null)
		{
			return false;
		}

		ClaimedChunk chunk = new ClaimedChunk(pos, player, 0);
		ClaimedChunkStorage.INSTANCE.setChunk(pos, chunk);
		new ChunkModifiedEvent.Claimed(chunk).post();
		return true;
	}

	public static boolean unclaimChunk(IForgePlayer player, ChunkDimPos pos)
	{
		IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);

		if (chunk != null)
		{
			setLoaded(player, pos, false);
			new ChunkModifiedEvent.Unclaimed(chunk).post();
			ClaimedChunkStorage.INSTANCE.setChunk(pos, null);
			return true;
		}

		return false;
	}

	public static void unclaimAllChunks(IForgePlayer player, @Nullable Integer dim)
	{
		for (IClaimedChunk chunk : ClaimedChunkStorage.INSTANCE.getChunks(player))
		{
			ChunkDimPos pos = chunk.getPos();
			if (dim == null || dim == pos.dim)
			{
				setLoaded(player, pos, false);
				new ChunkModifiedEvent.Unclaimed(chunk).post();
				ClaimedChunkStorage.INSTANCE.setChunk(pos, null);
			}
		}
	}

	public static boolean setLoaded(IForgePlayer player, ChunkDimPos pos, boolean flag)
	{
		IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);

		if (chunk == null || flag == chunk.hasUpgrade(ChunkUpgrade.LOADED) || !player.equalsPlayer(chunk.getOwner()))
		{
			return false;
		}

		if (flag)
		{
			if (player.getTeam() == null)
			{
				if (player.isOnline())
				{
					FTBLibNotifications.NO_TEAM.send(player.getPlayer());
				}

				return false;
			}

			if (!FTBUPermissions.allowDimension(player.getProfile(), pos.dim))
			{
				return false;
			}

			int max = FTBUtilitiesAPI.API.getRankConfig(player.getProfile(), FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();

			if (max == 0)
			{
				return false;
			}

			int loadedChunks = 0;

			for (IClaimedChunk c : ClaimedChunkStorage.INSTANCE.getChunks(player))
			{
				if (c.hasUpgrade(ChunkUpgrade.LOADED))
				{
					loadedChunks++;

					if (loadedChunks >= max)
					{
						return false;
					}
				}
			}
		}
		else
		{
			new ChunkModifiedEvent.Unloaded(chunk).post();
		}

		chunk.setHasUpgrade(ChunkUpgrade.LOADED, flag);
		FTBUChunkManager.INSTANCE.checkChunk(chunk, null);

		if (flag)
		{
			new ChunkModifiedEvent.Loaded(chunk).post();
		}

		return true;
	}
}
