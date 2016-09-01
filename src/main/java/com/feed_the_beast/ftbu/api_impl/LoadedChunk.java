package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunk;
import net.minecraft.util.math.ChunkPos;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public final class LoadedChunk implements ILoadedChunk
{
    private final ChunkPos pos;
    private final IForgePlayer owner;
    private boolean forced;

    public LoadedChunk(ChunkPos p, IForgePlayer o)
    {
        pos = p;
        owner = o;
    }

    @Override
    public ChunkPos getPos()
    {
        return pos;
    }

    @Override
    public IForgePlayer getOwner()
    {
        return owner;
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