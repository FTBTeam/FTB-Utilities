package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.internal.FTBLibStats;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;

/**
 * Created by LatvianModder on 03.10.2016.
 */
public class ClaimedChunk implements IClaimedChunk
{
    private final ChunkDimPos pos;
    private final IForgePlayer owner;
    private boolean loaded, forced;

    public ClaimedChunk(ChunkDimPos c, IForgePlayer p)
    {
        pos = c;
        owner = p;
    }

    @Override
    public ChunkDimPos getPos()
    {
        return pos;
    }

    @Override
    public IForgePlayer getOwner()
    {
        return owner;
    }

    @Override
    public boolean isLoaded()
    {
        return loaded;
    }

    @Override
    public void setLoaded(boolean v)
    {
        loaded = v;
    }

    @Override
    public boolean isActuallyLoaded()
    {
        boolean loaded = isLoaded();

        if(loaded)
        {
            if(!FTBUConfigWorld.CHUNK_LOADING.getBoolean())
            {
                loaded = false;
            }

            switch((ChunkloaderType) RankConfigAPI.getRankConfig(owner.getProfile(), FTBUPermissions.CHUNKLOADER_TYPE).getValue())
            {
                case ONLINE:
                    if(!owner.isOnline())
                    {
                        loaded = false;
                    }
                    break;
                case OFFLINE:
                    if(!owner.isOnline())
                    {
                        double max = RankConfigAPI.getRankConfig(owner.getProfile(), FTBUPermissions.CHUNKLOADER_OFFLINE_TIMER).getDouble();

                        if(max > 0D && FTBLibStats.getLastSeenDeltaInHours(owner.stats(), false) > max)
                        {
                            loaded = false;
                        }
                    }
                    break;
                default:
                    loaded = false;

            }
        }

        return loaded;
    }

    @Override
    public boolean isForced()
    {
        return forced;
    }

    @Override
    public void setForced(boolean v)
    {
        forced = v;
    }
}