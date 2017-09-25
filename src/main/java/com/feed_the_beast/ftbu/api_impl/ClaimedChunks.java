package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftbl.lib.internal.FTBLibNotifications;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunks;
import com.feed_the_beast.ftbu.api.events.ChunkModifiedEvent;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksUpdate;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class ClaimedChunks implements IClaimedChunks
{
	public static ClaimedChunks INSTANCE;

	public enum ClaimResult
	{
		SUCCESS,
		NO_TEAM,
		DIMENSION_BLOCKED,
		NO_POWER,
		ALREADY_CLAIMED
	}

	private static final Predicate<ClaimedChunk> REMOVE_CHUNK = ClaimedChunk::isInvalid;
	private static IntArrayList unloadQueue = new IntArrayList();

	public static void loadReflection()
	{
		Field field = ReflectionHelper.findField(DimensionManager.class, "unloadQueue");
		field.setAccessible(true);

		try
		{
			//TAKE THAT FORGE! (This might be a *really* bad idea, but I have to try it)
			//Cache this field so reflection doesnt have to called every tick
			unloadQueue = (IntArrayList) field.get(null);
		}
		catch (IllegalAccessException ex)
		{
		}
	}

	private final Collection<ClaimedChunk> pendingChunks = new HashSet<>();
	private final Map<ChunkDimPos, ClaimedChunk> map = new HashMap<>();
	private final Collection<ChunkDimPos> forced = new HashSet<>();
	private final IntOpenHashSet forcedDimensions = new IntOpenHashSet();
	private final Int2ObjectOpenHashMap<World> worldMap = new Int2ObjectOpenHashMap<>();
	public long nextChunkloaderUpdate;
	private boolean isDirty = true;

	@Override
	public void markDirty()
	{
		isDirty = true;
	}

	public void processQueue()
	{
		if (!pendingChunks.isEmpty())
		{
			for (ClaimedChunk chunk : pendingChunks)
			{
				ClaimedChunk prevChunk = map.put(chunk.getPos(), chunk);

				if (prevChunk != null && prevChunk != chunk)
				{
					prevChunk.setInvalid();
				}

				markDirty();
			}

			pendingChunks.clear();
		}

		if (!map.isEmpty() && map.values().removeIf(REMOVE_CHUNK))
		{
			markDirty();
		}
	}

	public void update(MinecraftServer server, long now)
	{
		if (nextChunkloaderUpdate <= now)
		{
			nextChunkloaderUpdate = now + CommonUtils.TICKS_MINUTE;
			markDirty();
		}

		processQueue();

		if (isDirty)
		{
			Collection<ChunkDimPos> prevForced = forced.isEmpty() ? Collections.emptySet() : new HashSet<>(forced);
			forced.clear();
			forcedDimensions.clear();

			if (FTBUConfig.world.chunk_claiming && FTBUConfig.world.chunk_loading)
			{
				for (ClaimedChunk chunk : getAllChunks())
				{
					boolean force = chunk.shouldForce();
					ChunkDimPos pos = chunk.getPos();

					if (force)
					{
						forced.add(pos);
						forcedDimensions.add(pos.dim);
					}

					if (FTBUConfig.world.log_chunkloading && force != prevForced.contains(pos))
					{
						String dimName;

						switch (pos.dim)
						{
							case 0:
								dimName = "Overworld";
								break;
							case -1:
								dimName = "Nether";
								break;
							case 1:
								dimName = "The End";
								break;
							default:
								dimName = "DIM_" + pos.dim;
						}

						FTBUFinals.LOGGER.info(chunk.getTeam().getTitle() + (force ? " forced " : " unforced ") + pos.posX + "," + pos.posZ + " in " + dimName); //LANG
					}
				}
			}

			for (EntityPlayerMP player : ServerUtils.getServer().getPlayerList().getPlayers())
			{
				ChunkDimPos playerPos = new ChunkDimPos(player);
				int startX = playerPos.posX - ChunkSelectorMap.TILES_GUI2;
				int startZ = playerPos.posZ - ChunkSelectorMap.TILES_GUI2;
				new MessageClaimedChunksUpdate(startX, startZ, player).sendTo(player);
				FTBUPlayerEventHandler.updateChunkMessage(player, playerPos);
			}

			isDirty = false;
		}

		if (FTBUConfig.world.chunk_claiming && FTBUConfig.world.chunk_loading && !forcedDimensions.isEmpty())
		{
			for (int dim : forcedDimensions)
			{
				unloadQueue.rem(dim);
				worldMap.put(dim, server.getWorld(dim));
			}

			for (ChunkDimPos pos : forced)
			{
				worldMap.get(pos.dim).getChunkFromChunkCoords(pos.posX, pos.posZ);
			}

			worldMap.clear();
		}
	}

	@Override
	@Nullable
	public ClaimedChunk getChunk(ChunkDimPos pos)
	{
		if (!FTBUConfig.world.chunk_claiming)
		{
			return null;
		}

		ClaimedChunk chunk = map.get(pos);
		return chunk == null || chunk.isInvalid() ? null : chunk;
	}

	public void removeChunk(ChunkDimPos pos)
	{
		ClaimedChunk prevChunk = map.get(pos);

		if (prevChunk != null)
		{
			prevChunk.setInvalid();
			markDirty();
		}
	}

	public void addChunk(ClaimedChunk chunk)
	{
		pendingChunks.add(chunk);
		markDirty();
	}

	@Override
	public Collection<ClaimedChunk> getAllChunks()
	{
		return !FTBUConfig.world.chunk_claiming ? Collections.emptyList() : getAllChunksIgnoreConfig();
	}

	@Override
	public Collection<ClaimedChunk> getAllChunksIgnoreConfig()
	{
		return map.isEmpty() ? Collections.emptyList() : map.values();
	}

	@Override
	public Collection<ClaimedChunk> getTeamChunks(@Nullable IForgeTeam team)
	{
		if (team == null)
		{
			return Collections.emptyList();
		}

		Collection<ClaimedChunk> c = new ArrayList<>();

		for (ClaimedChunk chunk : map.values())
		{
			if (!chunk.isInvalid() && team.equalsTeam(chunk.team.team))
			{
				c.add(chunk);
			}
		}

		return c;
	}

	@Override
	public Collection<ChunkDimPos> getForcedChunks()
	{
		return FTBUConfig.world.chunk_claiming ? forced : Collections.emptySet();
	}

	@Override
	public boolean canPlayerInteract(EntityPlayerMP player, EnumHand hand, BlockPosContainer block, BlockInteractionType type)
	{
		if (!FTBUConfig.world.chunk_claiming)
		{
			return true;
		}

		if (FTBUPermissions.canModifyBlock(player, hand, block, type))
		{
			return true;
		}

		ClaimedChunk chunk = getChunk(new ChunkDimPos(block.getPos(), player.dimension));

		if (chunk == null)
		{
			return true;
		}

		IForgePlayer p = FTBLibAPI.API.getUniverse().getPlayer(player);

		if (chunk.getTeam().getOwner().equalsPlayer(p))
		{
			return true;
		}

		if (p.isFake())
		{
			return chunk.team.fakePlayers.getBoolean();
		}

		return chunk.team.team.hasStatus(p.getId(), (type == BlockInteractionType.INTERACT ? chunk.team.interactWithBlocks : chunk.team.editBlocks).getValue());
	}

	public ClaimResult claimChunk(@Nullable FTBUTeamData data, ChunkDimPos pos)
	{
		if (!FTBUConfig.world.allowDimension(pos.dim))
		{
			return ClaimResult.DIMENSION_BLOCKED;
		}

		if (data == null)
		{
			return ClaimResult.NO_TEAM;
		}

		int max = data.getMaxClaimChunks();
		if (max == 0)
		{
			return ClaimResult.NO_POWER;
		}

		if (getTeamChunks(data.team).size() >= max)
		{
			return ClaimResult.NO_POWER;
		}

		ClaimedChunk chunk = getChunk(pos);

		if (chunk != null)
		{
			return ClaimResult.ALREADY_CLAIMED;
		}

		chunk = new ClaimedChunk(pos, data);
		addChunk(chunk);
		new ChunkModifiedEvent.Claimed(chunk).post();
		return ClaimResult.SUCCESS;
	}

	public boolean unclaimChunk(IForgePlayer player, ChunkDimPos pos)
	{
		ClaimedChunk chunk = map.get(pos);

		if (chunk != null && !chunk.isInvalid())
		{
			setLoaded(player, pos, false);
			new ChunkModifiedEvent.Unclaimed(chunk).post();
			removeChunk(pos);
			return true;
		}

		return false;
	}

	public void unclaimAllChunks(IForgePlayer player, @Nullable Integer dim)
	{
		for (ClaimedChunk chunk : getTeamChunks(player.getTeam()))
		{
			ChunkDimPos pos = chunk.getPos();
			if (dim == null || dim == pos.dim)
			{
				setLoaded(player, pos, false);
				new ChunkModifiedEvent.Unclaimed(chunk).post();
				removeChunk(pos);
			}
		}
	}

	public boolean setLoaded(IForgePlayer player, ChunkDimPos pos, boolean loaded)
	{
		ClaimedChunk chunk = getChunk(pos);

		if (chunk == null || loaded == chunk.hasUpgrade(ChunkUpgrades.LOADED) || !chunk.getTeam().equalsTeam(player.getTeam()))
		{
			return false;
		}

		if (loaded)
		{
			if (player.getTeam() == null)
			{
				if (player.isOnline())
				{
					FTBLibNotifications.NO_TEAM.send(player.getPlayer());
				}

				return false;
			}

			if (!FTBUConfig.world.allowDimension(pos.dim))
			{
				return false;
			}

			int max = FTBUtilitiesAPI.API.getRankConfig(player.getProfile(), FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();

			if (max == 0)
			{
				return false;
			}

			int loadedChunks = 0;

			for (ClaimedChunk c : getTeamChunks(player.getTeam()))
			{
				if (c.hasUpgrade(ChunkUpgrades.LOADED))
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

		chunk.setHasUpgrade(ChunkUpgrades.LOADED, loaded);

		if (loaded)
		{
			new ChunkModifiedEvent.Loaded(chunk).post();
		}

		markDirty();
		return true;
	}
}