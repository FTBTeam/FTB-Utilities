package com.feed_the_beast.ftbutilities.integration.kubejs;

import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import dev.latvian.kubejs.world.BlockContainerJS;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class KubeJSFTBUtilitiesWrapper
{
	public boolean isRanksActive()
	{
		return Ranks.isActive();
	}

	public Set<String> getRanks()
	{
		return Ranks.isActive() ? Ranks.INSTANCE.ranks.keySet() : Collections.emptySet();
	}

	@Nullable
	public Rank getRank(String id)
	{
		return Ranks.INSTANCE.getRank(id);
	}

	public void saveRanks()
	{
		if (Ranks.isActive())
		{
			Ranks.INSTANCE.save();
		}
	}

	public Map<String, Rank> getRankMap()
	{
		return Ranks.INSTANCE.ranks;
	}

	@Nullable
	public ClaimedChunk getClaimedChunk(ChunkDimPos pos)
	{
		if (ClaimedChunks.isActive())
		{
			return ClaimedChunks.instance.getChunk(pos);
		}

		return null;
	}

	@Nullable
	public ClaimedChunk getClaimedChunk(BlockContainerJS pos)
	{
		return getClaimedChunk(new ChunkDimPos(pos.getPos(), pos.getDimension()));
	}
}