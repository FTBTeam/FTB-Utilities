package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum ClaimedChunkStorage implements IClaimedChunkStorage
{
	INSTANCE;

	private static final Map<ChunkDimPos, IClaimedChunk> MAP = new HashMap<>();
	private static final Map<ChunkDimPos, IClaimedChunk> MAP_MIRROR = Collections.unmodifiableMap(MAP);

	public void init()
	{
		clear();
	}

	public void clear()
	{
		MAP.clear();
	}

	@Override
	@Nullable
	public IClaimedChunk getChunk(ChunkDimPos pos)
	{
		return MAP.get(pos);
	}

	@Override
	public void setChunk(ChunkDimPos pos, @Nullable IClaimedChunk chunk)
	{
		if (chunk == null)
		{
			MAP.remove(pos);
		}
		else
		{
			MAP.put(pos, chunk);
		}
	}

	@Override
	public Collection<IClaimedChunk> getChunks(@Nullable IForgePlayer owner)
	{
		if (MAP.isEmpty())
		{
			return Collections.emptyList();
		}
		else if (owner == null)
		{
			return MAP_MIRROR.values();
		}

		Collection<IClaimedChunk> c = new ArrayList<>();

		MAP.forEach((key, value) ->
		{
			if (value.getOwner().equalsPlayer(owner))
			{
				c.add(value);
			}
		});

		return c;
	}

	@Override
	public boolean canPlayerInteract(EntityPlayerMP ep, EnumHand hand, BlockPosContainer block, BlockInteractionType type)
	{
		if (FTBUPermissions.canModifyBlock(ep, hand, block, type))
		{
			return true;
		}

		IClaimedChunk chunk = getChunk(new ChunkDimPos(block.getPos(), ep.dimension));

		if (chunk == null)
		{
			return true;
		}

		IForgePlayer player = FTBLibAPI.API.getUniverse().getPlayer(ep);

		if (chunk.getOwner().equalsPlayer(player))
		{
			return true;
		}

		IForgeTeam team = chunk.getOwner().getTeam();

		if (team == null)
		{
			return true;
		}

		FTBUTeamData data = FTBUTeamData.get(team);

		if (player.isFake())
		{
			return data.fakePlayers.getBoolean();
		}

		return team.canInteract(player.getId(), (type == BlockInteractionType.INTERACT ? data.interactWithBlocks : data.editBlocks).getValue());
	}
}