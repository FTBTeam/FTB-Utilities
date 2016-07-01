package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.util.ChunkDimPos;

import javax.annotation.Nonnull;
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
    private final Map<ChunkDimPos, ClaimedChunk> map;
    private final Map<UUID, Collection<ClaimedChunk>> invertedMap;

    public ClaimedChunkStorage()
    {
        map = new HashMap<>();
        invertedMap = new HashMap<>();
    }

    @Nonnull
    public Collection<ClaimedChunk> getAllChunks()
    {
        return map.values();
    }

    @Nullable
    public ClaimedChunk getChunk(@Nonnull ChunkDimPos pos)
    {
        return map.get(pos);
    }

    public void put(@Nonnull ChunkDimPos pos, @Nullable ClaimedChunk c)
    {
        if(c == null)
        {
            ClaimedChunk chunk = map.remove(pos);

            if(chunk != null)
            {
                Collection<ClaimedChunk> ch = invertedMap.get(chunk.owner.getProfile().getId());

                if(ch != null)
                {
                    ch.remove(chunk);
                }
            }
        }
        else
        {
            map.put(c.pos, c);

            Collection<ClaimedChunk> ch = invertedMap.get(c.owner.getProfile().getId());

            if(ch == null)
            {
                ch = new HashSet<>();
                invertedMap.put(c.owner.getProfile().getId(), ch);
            }

            ch.add(c);
        }
    }

    @Nonnull
    public Collection<ClaimedChunk> getChunks(@Nonnull UUID playerID)
    {
        Collection<ClaimedChunk> c = invertedMap.get(playerID);
        return (c == null) ? Collections.EMPTY_SET : c;
    }

    public int getClaimedChunks(@Nonnull UUID playerID)
    {
        Collection<ClaimedChunk> c = invertedMap.get(playerID);
        return (c == null) ? 0 : c.size();
    }

    public int getLoadedChunks(@Nonnull UUID playerID)
    {
        Collection<ClaimedChunk> c = invertedMap.get(playerID);

        if(c == null || c.isEmpty())
        {
            return 0;
        }

        int loaded = 0;

        for(ClaimedChunk chunk : c)
        {
            if(chunk.loaded)
            {
                loaded++;
            }
        }

        return loaded;
    }

    public ForgePlayer getOwnerPlayer(ChunkDimPos pos)
    {
        ClaimedChunk c = map.get(pos);
        return (c == null) ? null : c.owner;
    }
}
