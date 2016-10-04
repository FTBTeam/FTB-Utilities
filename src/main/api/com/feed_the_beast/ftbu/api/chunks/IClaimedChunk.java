package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;

/**
 * Created by LatvianModder on 03.10.2016.
 */
public interface IClaimedChunk
{
    ChunkDimPos getPos();

    IForgePlayer getOwner();

    boolean isLoaded();

    void setLoaded(boolean v);

    boolean isActuallyLoaded();
    
    boolean isForced();

    void setForced(boolean v);
}