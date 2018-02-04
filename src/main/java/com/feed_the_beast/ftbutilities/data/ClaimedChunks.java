package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftblib.lib.math.BlockPosContainer;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftbutilities.FTBUConfig;
import com.feed_the_beast.ftbutilities.FTBUPermissions;
import com.feed_the_beast.ftbutilities.events.chunks.ChunkModifiedEvent;
import com.feed_the_beast.ftbutilities.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbutilities.net.MessageClaimedChunksUpdate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class ClaimedChunks
{
	public static ClaimedChunks instance;

	public final Universe universe;
	private final Collection<ClaimedChunk> pendingChunks = new HashSet<>();
	private final Map<ChunkDimPos, ClaimedChunk> map = new HashMap<>();
	public long nextChunkloaderUpdate;
	private boolean isDirty = true;

	public ClaimedChunks(Universe u)
	{
		universe = u;
	}

	@Nullable
	public ForgeTeam getChunkTeam(ChunkDimPos pos)
	{
		ClaimedChunk chunk = getChunk(pos);
		return chunk == null ? null : chunk.getTeam();
	}

	public void markDirty()
	{
		isDirty = true;
	}

	public void clear()
	{
		pendingChunks.clear();
		map.clear();
		nextChunkloaderUpdate = 0;
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

		Iterator<ClaimedChunk> iterator = map.values().iterator();

		while (iterator.hasNext())
		{
			ClaimedChunk chunk = iterator.next();

			if (chunk.isInvalid())
			{
				FTBULoadedChunkManager.INSTANCE.unforceChunk(chunk);
				iterator.remove();
			}
		}
	}


	public void update(MinecraftServer server, long now)
	{
		if (nextChunkloaderUpdate <= now)
		{
			nextChunkloaderUpdate = now + CommonUtils.TICKS_MINUTE;
			markDirty();
		}

		if (isDirty)
		{
			processQueue();

			if (FTBUConfig.world.chunk_loading)
			{
				for (ForgeTeam team : universe.getTeams())
				{
					FTBUTeamData.get(team).canForceChunks = FTBULoadedChunkManager.INSTANCE.canForceChunks(team);
				}

				for (ClaimedChunk chunk : getAllChunks())
				{
					boolean force = chunk.getData().canForceChunks && chunk.isLoaded();

					if (chunk.forced == null || chunk.forced != force)
					{
						if (force)
						{
							FTBULoadedChunkManager.INSTANCE.forceChunk(server, chunk);
						}
						else
						{
							FTBULoadedChunkManager.INSTANCE.unforceChunk(chunk);
						}
					}
				}
			}

			for (EntityPlayerMP player : server.getPlayerList().getPlayers())
			{
				ChunkDimPos playerPos = new ChunkDimPos(player);
				int startX = playerPos.posX - ChunkSelectorMap.TILES_GUI2;
				int startZ = playerPos.posZ - ChunkSelectorMap.TILES_GUI2;
				new MessageClaimedChunksUpdate(startX, startZ, player).sendTo(player);
				FTBUPlayerEventHandler.updateChunkMessage(player, playerPos);
			}

			isDirty = false;
		}
	}

	@Nullable
	public ClaimedChunk getChunk(ChunkDimPos pos)
	{
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

	public Collection<ClaimedChunk> getAllChunks()
	{
		return map.isEmpty() ? Collections.emptyList() : map.values();
	}

	public Set<ClaimedChunk> getTeamChunks(@Nullable ForgeTeam team, boolean includePending)
	{
		if (team == null)
		{
			return Collections.emptySet();
		}

		Set<ClaimedChunk> set = new HashSet<>();

		for (ClaimedChunk chunk : map.values())
		{
			if (!chunk.isInvalid() && team.equalsTeam(chunk.getTeam()))
			{
				set.add(chunk);
			}
		}

		if (includePending)
		{
			for (ClaimedChunk chunk : pendingChunks)
			{
				if (team.equalsTeam(chunk.getTeam()))
				{
					set.add(chunk);
				}
			}
		}

		return set;
	}

	public Set<ClaimedChunk> getTeamChunks(@Nullable ForgeTeam team)
	{
		return getTeamChunks(team, false);
	}

	public boolean canPlayerInteract(EntityPlayerMP player, EnumHand hand, BlockPosContainer block, BlockInteractionType type)
	{
		if (ClaimedChunks.instance == null)
		{
			return true;
		}

		if (FTBUPermissions.canModifyBlock(player, hand, block, type))
		{
			return true;
		}

		ClaimedChunk chunk = getChunk(new ChunkDimPos(block.getPos(), player.dimension));
		return chunk == null || chunk.getTeam().hasStatus(universe.getPlayer(player), chunk.getData().getStatusFromType(type));
	}

	public boolean canPlayerAttackEntity(EntityPlayerMP player, Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			if (FTBUConfig.world.safe_spawn && player.dimension == 0 && FTBUUniverseData.isInSpawn(player.mcServer, new ChunkDimPos(entity)))
			{
				return false;
			}

			if (FTBUConfig.world.enable_pvp.isDefault())
			{
				boolean pvp1 = FTBUPlayerData.get(universe.getPlayer(player)).enablePVP.getBoolean();
				boolean pvp2 = FTBUPlayerData.get(universe.getPlayer(entity)).enablePVP.getBoolean();
				return pvp1 && pvp2;
			}

			return FTBUConfig.world.enable_pvp.isTrue();
		}

		if (!(entity instanceof IMob))
		{
			ClaimedChunk chunk = getChunk(new ChunkDimPos(entity));
			return chunk == null || chunk.getTeam().hasStatus(universe.getPlayer(player), chunk.getData().getAttackEntitiesStatus());
		}

		return true;
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
		if (max == 0 || getTeamChunks(data.team, true).size() >= max)
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

	public boolean unclaimChunk(ForgeTeam team, ChunkDimPos pos)
	{
		ClaimedChunk chunk = map.get(pos);

		if (chunk != null && !chunk.isInvalid())
		{
			setLoaded(team, pos, false);
			new ChunkModifiedEvent.Unclaimed(chunk).post();
			removeChunk(pos);
			return true;
		}

		return false;
	}

	public void unclaimAllChunks(ForgeTeam team, @Nullable Integer dim)
	{
		for (ClaimedChunk chunk : getTeamChunks(team))
		{
			ChunkDimPos pos = chunk.getPos();
			if (dim == null || dim == pos.dim)
			{
				setLoaded(team, pos, false);
				new ChunkModifiedEvent.Unclaimed(chunk).post();
				removeChunk(pos);
			}
		}
	}

	public boolean setLoaded(ForgeTeam team, ChunkDimPos pos, boolean loaded)
	{
		ClaimedChunk chunk = getChunk(pos);

		if (chunk == null || loaded == chunk.isLoaded() || !chunk.getTeam().equalsTeam(team))
		{
			return false;
		}

		if (loaded)
		{
			if (!FTBUConfig.world.allowDimension(pos.dim))
			{
				return false;
			}

			int max = FTBUTeamData.get(team).getMaxChunkloaderChunks();

			if (max == 0)
			{
				return false;
			}

			int loadedChunks = 0;

			for (ClaimedChunk c : getTeamChunks(team))
			{
				if (c.isLoaded())
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

		if (chunk.setLoaded(loaded) && loaded)
		{
			new ChunkModifiedEvent.Loaded(chunk).post();
		}

		return true;
	}
}