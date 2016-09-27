package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunkStorage;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface FTBUtilitiesAPI
{
    IClaimedChunkStorage getClaimedChunks();

    ILoadedChunkStorage getLoadedChunks();
}