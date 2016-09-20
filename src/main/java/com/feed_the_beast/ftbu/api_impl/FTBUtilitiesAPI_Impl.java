package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAddon;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesAddon;
import com.feed_the_beast.ftbu.api.ILeaderboardRegistry;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunkStorage;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public enum FTBUtilitiesAPI_Impl implements FTBUtilitiesAPI
{
    INSTANCE;

    public void init(ASMDataTable table)
    {
        for(ASMDataTable.ASMData data : table.getAll(FTBUtilitiesAddon.class.getName()))
        {
            try
            {
                Class<?> clazz = Class.forName(data.getClassName());
                Class<? extends IFTBUtilitiesAddon> clazzAddon = clazz.asSubclass(IFTBUtilitiesAddon.class);
                clazzAddon.newInstance().onFTBUtilitiesLoaded(this);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public ILeaderboardRegistry getLeaderboardRegistry()
    {
        return LeaderboardRegistry.INSTANCE;
    }

    @Override
    public IClaimedChunkStorage getClaimedChunks()
    {
        return ClaimedChunkStorage.INSTANCE;
    }

    @Override
    public ILoadedChunkStorage getLoadedChunks()
    {
        return LoadedChunkStorage.INSTANCE;
    }
}