package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunkStorage;
import com.mojang.authlib.GameProfile;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface FTBUtilitiesAPI
{
    IClaimedChunkStorage getClaimedChunks();

    ILoadedChunkStorage getLoadedChunks();

    IRank getRank(GameProfile profile);
}