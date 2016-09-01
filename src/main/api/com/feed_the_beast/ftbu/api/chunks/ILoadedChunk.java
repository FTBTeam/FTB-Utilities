package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import net.minecraft.util.math.ChunkPos;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface ILoadedChunk
{
    ChunkPos getPos();

    IForgePlayer getOwner();

    boolean isForced();

    void setForced(boolean forced);
}