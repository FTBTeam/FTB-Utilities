package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.mojang.authlib.GameProfile;

import java.util.Collection;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface FTBUtilitiesAPI
{
    Collection<IFTBUtilitiesPlugin> getAllPlugins();

    IClaimedChunkStorage getClaimedChunks();

    IRank getRank(GameProfile profile);
}