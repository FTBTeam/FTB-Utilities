package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface ILoadedChunkStorage
{
    @Nullable
    ILoadedChunk getChunk(ChunkDimPos pos);

    boolean isLoaded(ChunkDimPos pos, @Nullable IForgePlayer player);

    void setLoaded(ChunkDimPos pos, @Nullable IForgePlayer player);

    Collection<ChunkDimPos> getChunks(@Nullable IForgePlayer player);

    void checkUnloaded(Integer dimID);

    int getLoadedChunks(@Nullable IForgePlayer player);
}