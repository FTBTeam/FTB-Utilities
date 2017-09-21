package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;

/**
 * @author LatvianModder
 */
public interface IClaimedChunk
{
	boolean isInvalid();

	ChunkDimPos getPos();

	IForgeTeam getTeam();

	boolean hasUpgrade(IChunkUpgrade upgrade);

	boolean setHasUpgrade(IChunkUpgrade upgrade, boolean v);
}