package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.client.FTBUClient;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.DimensionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUWorldDataSP extends FTBUWorldData
{
    public Map<ChunkDimPos, ChunkType> chunks;

    public static boolean isLoaded()
    {
        return ForgeWorldSP.inst != null && ForgeWorldSP.inst.hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null);
    }

    public static FTBUWorldDataSP get()
    {
        return isLoaded() ? (FTBUWorldDataSP) ForgeWorldSP.inst.getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null) : null;
    }

    @Override
    public void onLoaded()
    {
        chunks = new HashMap<>();
    }

    public ChunkType getType(ChunkCoordIntPair pos)
    {
        if(pos == null) { return ChunkType.UNLOADED; }
        ChunkType i = chunks.get(pos);
        return (i == null) ? ChunkType.UNLOADED : i;
    }

    public void setTypes(DimensionType dim, Map<ChunkDimPos, ChunkType> types)
    {
        chunks.putAll(types);
        if(FTBUClient.journeyMapHandler != null) { FTBUClient.journeyMapHandler.refresh(dim.getId()); }
    }
}
