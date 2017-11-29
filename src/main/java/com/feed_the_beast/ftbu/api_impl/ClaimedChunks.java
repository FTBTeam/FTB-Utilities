package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api.chunks.ChunkModifiedEvent;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunks;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksUpdate;
import com.feed_the_beast.ftbu.util.FTBUPlayerData;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class ClaimedChunks implements IClaimedChunks, ForgeChunkManager.LoadingCallback
{
	public static final ClaimedChunks INSTANCE = new ClaimedChunks();

	public enum ClaimResult
	{
		SUCCESS,
		NO_TEAM,
		DIMENSION_BLOCKED,
		NO_POWER,
		ALREADY_CLAIMED
	}

	public static final class TicketKey
	{
		public final int dimension;
		public final String teamId;

		public TicketKey(int dim, String team)
		{
			dimension = dim;
			teamId = team;
		}

		public int hashCode()
		{
			return Objects.hash(dimension, teamId);
		}

		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			else if (o != null && o.getClass() == TicketKey.class)
			{
				TicketKey key = (TicketKey) o;
				return dimension == key.dimension && teamId.equals(key.teamId);
			}
			return false;
		}
	}

	private final Collection<ClaimedChunk> pendingChunks = new HashSet<>();
	private final Map<ChunkDimPos, ClaimedChunk> map = new HashMap<>();
	private final Map<TicketKey, ForgeChunkManager.Ticket> ticketMap = new HashMap<>();
	private final Map<ChunkDimPos, ForgeChunkManager.Ticket> chunkTickets = new HashMap<>();
	public long nextChunkloaderUpdate;
	private boolean isDirty = true;

	private ClaimedChunks()
	{
	}

	@Override
	public void markDirty()
	{
		isDirty = true;
	}

	public void clear()
	{
		pendingChunks.clear();
		map.clear();
		ticketMap.clear();
		chunkTickets.clear();
		nextChunkloaderUpdate = 0;
		isDirty = true;
	}

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		for (ForgeChunkManager.Ticket ticket : tickets)
		{
			TicketKey key = new TicketKey(world.provider.getDimension(), ticket.getModData().getString("Team"));

			if (!key.teamId.isEmpty())
			{
				ForgeChunkManager.releaseTicket(ticket);
				ForgeChunkManager.Ticket removed = ticketMap.remove(key);

				if (removed != null && removed != ticket)
				{
					ForgeChunkManager.releaseTicket(removed);
				}
			}
		}
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
				unforceChunk(chunk);
				iterator.remove();
			}
		}
	}

	@Nullable
	private ForgeChunkManager.Ticket requestTicket(TicketKey key)
	{
		ForgeChunkManager.Ticket ticket = ticketMap.get(key);

		if (ticket == null && DimensionManager.isDimensionRegistered(key.dimension))
		{
			WorldServer worldServer = ServerUtils.getServer().getWorld(key.dimension);
			ticket = ForgeChunkManager.requestTicket(FTBU.INST, worldServer, ForgeChunkManager.Type.NORMAL);

			if (ticket != null)
			{
				ticketMap.put(key, ticket);
				ticket.getModData().setString("Team", key.teamId);
			}
		}

		return ticket;
	}

	private void forceChunk(ClaimedChunk chunk)
	{
		if (chunk.forced != null && chunk.forced)
		{
			return;
		}

		ChunkDimPos pos = chunk.getPos();
		ForgeChunkManager.Ticket ticket = requestTicket(new TicketKey(pos.dim, chunk.getTeam().getName()));

		if (ticket == null)
		{
			return;
		}

		ChunkPos chunkPos = pos.getChunkPos();
		ForgeChunkManager.unforceChunk(ticket, chunkPos);
		ForgeChunkManager.forceChunk(ticket, chunkPos);
		chunk.forced = true;
		chunkTickets.put(pos, ticket);

		if (FTBUConfig.world.log_chunkloading)
		{
			FTBUFinals.LOGGER.info(FTBULang.CHUNKS_CHUNKLOADER_FORCED.translate(chunk.getTeam().getTitle(), pos.posX, pos.posZ, ServerUtils.getDimensionName(null, pos.dim)));
		}
	}

	private void unforceChunk(ClaimedChunk chunk)
	{
		if (chunk.forced != null && !chunk.forced)
		{
			return;
		}

		ChunkDimPos pos = chunk.getPos();
		ForgeChunkManager.Ticket ticket = chunkTickets.get(pos);

		if (ticket == null)
		{
			return;
		}

		ForgeChunkManager.unforceChunk(ticket, pos.getChunkPos());
		chunkTickets.remove(pos);
		chunk.forced = false;

		if (ticket.getChunkList().isEmpty())
		{
			ticketMap.remove(new TicketKey(pos.dim, chunk.getTeam().getName()));
			ForgeChunkManager.releaseTicket(ticket);
		}

		if (FTBUConfig.world.log_chunkloading)
		{
			FTBUFinals.LOGGER.info(FTBULang.CHUNKS_CHUNKLOADER_UNFORCED.translate(chunk.getTeam().getTitle(), pos.posX, pos.posZ, ServerUtils.getDimensionName(null, pos.dim)));
		}
	}

	private boolean canForceChunks(IForgeTeam team)
	{
		Collection<IForgePlayer> members = team.getMembers();

		for (IForgePlayer player : members)
		{
			if (player.isOnline())
			{
				return true;
			}
		}

		for (IForgePlayer player : members)
		{
			if (PermissionAPI.hasPermission(player.getProfile(), FTBUPermissions.CHUNKLOADER_LOAD_OFFLINE, null))
			{
				return true;
			}
		}

		return false;
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

			if (FTBUConfig.world.chunk_claiming && FTBUConfig.world.chunk_loading)
			{
				for (IForgeTeam team : FTBLibAPI.API.getUniverse().getTeams())
				{
					FTBUTeamData.get(team).canForceChunks = canForceChunks(team);
				}

				for (ClaimedChunk chunk : getAllChunks())
				{
					boolean force = chunk.getData().canForceChunks && chunk.hasUpgrade(ChunkUpgrades.LOADED);

					if (chunk.forced == null || chunk.forced != force)
					{
						if (force)
						{
							forceChunk(chunk);
						}
						else
						{
							unforceChunk(chunk);
						}
					}
				}
			}

			for (EntityPlayerMP player : ServerUtils.getPlayers())
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
	public Set<ClaimedChunk> getTeamChunks(@Nullable IForgeTeam team, boolean includePending)
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

	@Override
	public Set<ClaimedChunk> getTeamChunks(@Nullable IForgeTeam team)
	{
		return getTeamChunks(team, false);
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
		return chunk == null || chunk.getTeam().hasStatus(FTBLibAPI.API.getUniverse().getPlayer(player), chunk.getData().getStatusFromType(type));
	}

	@Override
	public boolean canPlayerAttackEntity(EntityPlayerMP player, Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			if (FTBUConfig.world.safe_spawn && player.dimension == 0 && FTBUUniverseData.isInSpawn(new ChunkDimPos(entity)))
			{
				return false;
			}

			if (FTBUConfig.world.enable_pvp.isDefault())
			{
				boolean pvp1 = FTBUPlayerData.get(FTBLibAPI.API.getUniverse().getPlayer(player)).enablePVP.getBoolean();
				boolean pvp2 = FTBUPlayerData.get(FTBLibAPI.API.getUniverse().getPlayer(entity)).enablePVP.getBoolean();
				return pvp1 && pvp2;
			}

			return FTBUConfig.world.enable_pvp.isTrue();
		}

		if (!(entity instanceof IMob))
		{
			ClaimedChunk chunk = getChunk(new ChunkDimPos(entity));
			return chunk == null || chunk.getTeam().hasStatus(FTBLibAPI.API.getUniverse().getPlayer(player), chunk.getData().getAttackEntitiesStatus());
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

	public boolean unclaimChunk(IForgeTeam team, ChunkDimPos pos)
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

	public void unclaimAllChunks(IForgeTeam team, @Nullable Integer dim)
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

	public boolean setLoaded(IForgeTeam team, ChunkDimPos pos, boolean loaded)
	{
		ClaimedChunk chunk = getChunk(pos);

		if (chunk == null || loaded == chunk.hasUpgrade(ChunkUpgrades.LOADED) || !chunk.getTeam().equalsTeam(team))
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