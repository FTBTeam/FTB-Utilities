package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.client.FTBUClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUWorldDataSP extends FTBUWorldData
{
    public static Map<ChunkDimPos, ChunkType> chunks;

    public static ChunkType getType(ChunkDimPos pos)
    {
        return (pos != null && chunks != null && chunks.containsKey(pos)) ? chunks.get(pos) : ChunkType.UNLOADED;
    }

    @SideOnly(Side.CLIENT)
    public static void setTypes(Map<ChunkDimPos, ChunkType> types)
    {
        if(chunks == null)
        {
            return;
        }

        chunks.putAll(types);

        if(FTBUClient.journeyMapHandler != null)
        {
            for(Map.Entry<ChunkDimPos, ChunkType> e : types.entrySet())
            {
                FTBUClient.journeyMapHandler.chunkChanged(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public void onLoaded()
    {
        chunks = new HashMap<>();
    }

    @Override
    public void onClosed()
    {
        chunks = null;
    }
}
