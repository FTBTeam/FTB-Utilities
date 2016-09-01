package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunkStorage;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public abstract class FTBUtilitiesAPI
{
    private static FTBUtilitiesAPI API;

    public static void setAPI(FTBUtilitiesAPI api)
    {
        API = api;
    }

    public static FTBUtilitiesAPI get()
    {
        return API;
    }

    public abstract ILeaderboardRegistry getLeaderboardRegistry();

    public abstract IClaimedChunkStorage getClaimedChunks();

    public abstract ILoadedChunkStorage getLoadedChunks();
}