package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunks;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum ClaimedChunks implements IClaimedChunks, ForgeChunkManager.LoadingCallback, ForgeChunkManager.OrderedLoadingCallback
{
	INSTANCE;

	private final Map<ChunkDimPos, ClaimedChunk> map = new HashMap<>();
	private final Map<ChunkDimPos, ClaimedChunk> mapMirror = Collections.unmodifiableMap(map);
	private final TIntObjectHashMap<ForgeChunkManager.Ticket> tickets = new TIntObjectHashMap<>();
	public long nextChunkloaderUpdate;

	public void initTicketConfig()
	{
		if (!ForgeChunkManager.getConfig().hasCategory(FTBUFinals.MOD_ID))
		{
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumChunksPerTicket", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}

		ForgeChunkManager.setForcedChunkLoadingCallback(FTBU.INST, this);
	}

	public void clear()
	{
		map.clear();
		tickets.clear();
		nextChunkloaderUpdate = 0;
	}

	@Nullable
	private ForgeChunkManager.Ticket request(World world, boolean createNew)
	{
		ForgeChunkManager.Ticket ticket = tickets.get(world.provider.getDimension());

		if (ticket == null && createNew)
		{
			ticket = ForgeChunkManager.requestTicket(FTBU.INST, world, ForgeChunkManager.Type.NORMAL);
			tickets.put(world.provider.getDimension(), ticket);
		}

		return ticket;
	}

	@Override
	public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount)
	{
		return Collections.emptyList();
	}

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets0, World world)
	{
		int dim = world.provider.getDimension();

		if (CommonUtils.DEV_ENV)
		{
			FTBUFinals.LOGGER.info("Loaded chunks " + dim);
		}

		ForgeChunkManager.Ticket ticket = tickets.get(dim);

		if (ticket != null && ticket.world != null && ticket.getModId() != null)
		{
			ForgeChunkManager.releaseTicket(ticket);
		}
		else if (ticket != null)
		{
			FTBUFinals.LOGGER.warn("Damaged ticket found: " + ticket + ", world:" + ticket.world + ", modID:" + ticket.getModId()); //LANG
		}

		tickets.remove(dim);

		if (tickets0.size() == 1)
		{
			tickets.put(dim, tickets0.get(0));
			checkDimension(world);
		}
		else if (tickets0.size() > 1)
		{
			FTBUFinals.LOGGER.warn("There was an error while loading tickets! Releasing all [" + tickets0.size() + "]!"); //LANG
			new ArrayList<>(tickets0).forEach(ForgeChunkManager::releaseTicket);
		}
	}

	public void update(long now)
	{
		if (nextChunkloaderUpdate <= now)
		{
			nextChunkloaderUpdate = now + CommonUtils.TICKS_MINUTE;
			checkAll();
		}
	}

	@Override
	@Nullable
	public ClaimedChunk getChunk(ChunkDimPos pos)
	{
		return map.get(pos);
	}

	public void setChunk(ChunkDimPos pos, @Nullable ClaimedChunk chunk)
	{
		if (chunk == null)
		{
			map.remove(pos);
		}
		else
		{
			map.put(pos, chunk);
		}
	}

	@Override
	public Collection<ClaimedChunk> getChunks(@Nullable IForgePlayer owner)
	{
		if (map.isEmpty())
		{
			return Collections.emptyList();
		}
		else if (owner == null)
		{
			return mapMirror.values();
		}

		Collection<ClaimedChunk> c = new ArrayList<>();

		for (ClaimedChunk chunk : map.values())
		{
			if (chunk.getOwner().equalsPlayer(owner))
			{
				c.add(chunk);
			}
		}

		return c;
	}

	@Override
	public boolean canPlayerInteract(EntityPlayerMP player, EnumHand hand, BlockPosContainer block, BlockInteractionType type)
	{
		if (FTBUPermissions.canModifyBlock(player, hand, block, type))
		{
			return true;
		}

		IClaimedChunk chunk = getChunk(new ChunkDimPos(block.getPos(), player.dimension));

		if (chunk == null)
		{
			return true;
		}

		IForgePlayer p = FTBLibAPI.API.getUniverse().getPlayer(player);

		if (chunk.getOwner().equalsPlayer(p))
		{
			return true;
		}

		IForgeTeam team = chunk.getOwner().getTeam();

		if (team == null)
		{
			return true;
		}

		FTBUTeamData data = FTBUTeamData.get(team);

		if (p.isFake())
		{
			return data.fakePlayers.getBoolean();
		}

		return team.hasStatus(p.getId(), (type == BlockInteractionType.INTERACT ? data.interactWithBlocks : data.editBlocks).getValue());
	}

	public void checkAll()
	{
		for (IClaimedChunk chunk : getChunks(null))
		{
			checkChunk(ServerUtils.getServer().getWorld(chunk.getPos().dim), chunk, null);
		}
	}

	public void checkDimension(World world)
	{
		ForgeChunkManager.Ticket ticket = request(world, false);

		for (IClaimedChunk chunk : getChunks(null))
		{
			if (chunk.getPos().dim == world.provider.getDimension())
			{
				checkChunk(world, chunk, ticket);
			}
		}
	}

	public void checkChunk(World world, IClaimedChunk chunk, @Nullable ForgeChunkManager.Ticket ticket)
	{
		boolean force = chunk.hasUpgrade(ChunkUpgrade.SHOULD_FORCE);

		if (force != chunk.hasUpgrade(ChunkUpgrade.FORCED))
		{
			chunk.setHasUpgrade(ChunkUpgrade.FORCED, force);

			if (ticket == null)
			{
				ticket = request(world, force);
			}

			if (ticket != null)
			{
				ChunkPos pos = chunk.getPos().getChunkPos();

				if (force)
				{
					if (!ticket.getChunkList().contains(pos))
					{
						ForgeChunkManager.forceChunk(ticket, pos);

						if (FTBUConfig.world.log_chunkloading)
						{
							FTBUFinals.LOGGER.info("Chunkloader forced " + chunk.getPos() + " by " + chunk.getOwner()); //LANG
						}
					}
				}
				else
				{
					if (ticket.getChunkList().contains(pos) && ticket.world != null)
					{
						ForgeChunkManager.unforceChunk(ticket, pos);

						if (FTBUConfig.world.log_chunkloading)
						{
							FTBUFinals.LOGGER.info("Chunkloader unforced " + chunk.getPos() + " by " + chunk.getOwner()); //LANG
						}
					}
				}
			}
		}
	}
}