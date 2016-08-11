package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbu.world.chunks.ClaimedChunk;
import com.latmod.lib.math.ChunkDimPos;

/**
 * Created by LatvianModder on 07.02.2016.
 */
public interface IJMPluginHandler
{
    void mappingStarted();

    void mappingStopped();

    void chunkChanged(ChunkDimPos pos, ClaimedChunk chunk);
}
