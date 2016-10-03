package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;

/**
 * Created by LatvianModder on 03.10.2016.
 */
public class ClaimedChunk implements IClaimedChunk
{
    private static final byte FLAG_LOADED = 1;
    private static final byte FLAG_FORCED = 2;

    private final ChunkDimPos pos;
    private final IForgePlayer owner;
    private byte flags;

    public ClaimedChunk(ChunkDimPos c, IForgePlayer p, int f)
    {
        pos = c;
        owner = p;
        flags = 0;
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
        return Bits.getFlag(flags, FLAG_LOADED);
    }

    public void setLoaded(boolean v)
    {
        flags = Bits.setFlag(flags, FLAG_LOADED, v);
    }

    @Override
    public boolean isForced()
    {
        return Bits.getFlag(flags, FLAG_FORCED);
    }

    public void setForced(boolean v)
    {
        flags = Bits.setFlag(flags, FLAG_FORCED, v);
    }

    public int getFlags()
    {
        return Bits.setFlag(flags, FLAG_FORCED, false);
    }
}