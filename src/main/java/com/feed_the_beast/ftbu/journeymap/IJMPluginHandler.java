package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.world.ClaimedChunk;

/**
 * Created by LatvianModder on 07.02.2016.
 */
public interface IJMPluginHandler
{
    void mappingStarted();

    void mappingStopped();

    void chunkChanged(ChunkDimPos pos, ClaimedChunk chunk);
}
