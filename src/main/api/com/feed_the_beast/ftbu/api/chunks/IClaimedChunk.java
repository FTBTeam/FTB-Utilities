package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;

/**
 * @author LatvianModder
 */
public interface IClaimedChunk
{
    ChunkDimPos getPos();

    IForgePlayer getOwner();

    boolean hasUpgrade(IChunkUpgrade upgrade);

    void setHasUpgrade(IChunkUpgrade upgrade, boolean v);
}