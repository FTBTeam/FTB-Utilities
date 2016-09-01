package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.ILeaderboardRegistry;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunkStorage;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class FTBUtilitiesAPI_Impl extends FTBUtilitiesAPI
{
    @Override
    public ILeaderboardRegistry getLeaderboardRegistry()
    {
        return LeaderboardRegistry.INSTANCE;
    }

    @Override
    public IClaimedChunkStorage getClaimedChunks()
    {
        return ClaimedChunkStorage.INSTANCE;
    }

    @Override
    public ILoadedChunkStorage getLoadedChunks()
    {
        return LoadedChunkStorage.INSTANCE;
    }
}