package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LatvianModder on 11.09.2016.
 */
public abstract class ModifyChunkEvent extends Event
{
    private final ChunkDimPos chunkPos;
    private final IForgePlayer forgePlayer;

    public ModifyChunkEvent(ChunkDimPos pos, IForgePlayer p)
    {
        chunkPos = pos;
        forgePlayer = p;
    }

    public ChunkDimPos getChunkPos()
    {
        return chunkPos;
    }

    public EntityPlayerMP getPlayer()
    {
        return forgePlayer.getPlayer();
    }

    public IForgePlayer getForgePlayer()
    {
        return forgePlayer;
    }

    public static class Claimed extends ModifyChunkEvent
    {
        public Claimed(ChunkDimPos pos, IForgePlayer p)
        {
            super(pos, p);
        }
    }

    public static class Unclaimed extends ModifyChunkEvent
    {
        public Unclaimed(ChunkDimPos pos, IForgePlayer p)
        {
            super(pos, p);
        }
    }

    public static class Loaded extends ModifyChunkEvent
    {
        public Loaded(ChunkDimPos pos, IForgePlayer p)
        {
            super(pos, p);
        }
    }

    public static class Unloaded extends ModifyChunkEvent
    {
        public Unloaded(ChunkDimPos pos, IForgePlayer p)
        {
            super(pos, p);
        }
    }
}