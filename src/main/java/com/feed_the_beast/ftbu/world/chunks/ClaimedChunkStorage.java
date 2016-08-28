package com.feed_the_beast.ftbu.world.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbu.api.IClaimedChunk;
import com.latmod.lib.math.ChunkDimPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 07.06.2016.
 */
public final class ClaimedChunkStorage
{
    private final Map<ChunkDimPos, IClaimedChunk> map;
    private final Map<UUID, Collection<IClaimedChunk>> invertedMap;

    public ClaimedChunkStorage()
    {
        map = new HashMap<>();
        invertedMap = new HashMap<>();
    }

    public Collection<IClaimedChunk> getAllChunks()
    {
        return map.values();
    }

    @Nullable
    public IClaimedChunk getChunk(ChunkDimPos pos)
    {
        return map.get(pos);
    }

    public void put(ChunkDimPos pos, @Nullable IClaimedChunk c)
    {
        if(c == null)
        {
            IClaimedChunk chunk = map.remove(pos);

            if(chunk != null)
            {
                Collection<IClaimedChunk> ch = invertedMap.get(chunk.getOwner().getProfile().getId());

                if(ch != null)
                {
                    ch.remove(chunk);
                }
            }
        }
        else
        {
            map.put(c.getPos(), c);

            Collection<IClaimedChunk> ch = invertedMap.get(c.getOwner().getProfile().getId());

            if(ch == null)
            {
                ch = new HashSet<>();
                invertedMap.put(c.getOwner().getProfile().getId(), ch);
            }

            ch.add(c);
        }
    }

    public Collection<IClaimedChunk> getChunks(UUID playerID)
    {
        Collection<IClaimedChunk> c = invertedMap.get(playerID);
        return (c == null) ? Collections.EMPTY_SET : c;
    }

    public int getClaimedChunks(UUID playerID)
    {
        Collection<IClaimedChunk> c = invertedMap.get(playerID);
        return (c == null) ? 0 : c.size();
    }

    public int getLoadedChunks(UUID playerID)
    {
        Collection<IClaimedChunk> c = invertedMap.get(playerID);

        if(c == null || c.isEmpty())
        {
            return 0;
        }

        int loaded = 0;

        for(IClaimedChunk chunk : c)
        {
            if(chunk.isLoaded())
            {
                loaded++;
            }
        }

        return loaded;
    }

    public IForgePlayer getOwnerPlayer(ChunkDimPos pos)
    {
        IClaimedChunk c = map.get(pos);
        return (c == null) ? null : c.getOwner();
    }
}
