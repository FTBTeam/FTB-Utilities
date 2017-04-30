package com.feed_the_beast.ftbu.api.events;

import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LatvianModder on 11.09.2016.
 */
public abstract class ChunkModifiedEvent extends Event
{
    private final IClaimedChunk chunk;

    public ChunkModifiedEvent(IClaimedChunk c)
    {
        chunk = c;
    }

    public IClaimedChunk getChunk()
    {
        return chunk;
    }

    public static class Claimed extends ChunkModifiedEvent
    {
        public Claimed(IClaimedChunk c)
        {
            super(c);
        }
    }

    public static class Unclaimed extends ChunkModifiedEvent
    {
        public Unclaimed(IClaimedChunk c)
        {
            super(c);
        }
    }

    public static class Loaded extends ChunkModifiedEvent
    {
        public Loaded(IClaimedChunk c)
        {
            super(c);
        }
    }

    public static class Unloaded extends ChunkModifiedEvent
    {
        public Unloaded(IClaimedChunk c)
        {
            super(c);
        }
    }
}