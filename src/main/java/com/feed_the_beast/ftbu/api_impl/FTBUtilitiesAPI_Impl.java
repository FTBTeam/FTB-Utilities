package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.lib.util.ASMUtils;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAddon;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunkStorage;
import com.feed_the_beast.ftbu.api.leaderboard.ILeaderboard;
import com.feed_the_beast.ftbu.api.leaderboard.Leaderboard;
import net.minecraft.stats.StatBase;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public enum FTBUtilitiesAPI_Impl implements FTBUtilitiesAPI
{
    INSTANCE;

    public final Map<StatBase, ILeaderboard> LEADERBOARDS = new HashMap<>();

    public void init(ASMDataTable table)
    {
        ASMUtils.findAnnotatedObjects(table, FTBUtilitiesAPI.class, FTBUtilitiesAddon.class, (obj, field, data) -> field.set(null, INSTANCE));
        ASMUtils.findAnnotatedMethods(table, FTBUtilitiesAddon.class, (method, params, data) -> method.invoke(null));
        ASMUtils.findAnnotatedObjects(table, ILeaderboard.class, Leaderboard.class, (obj, field, data) -> LEADERBOARDS.put(obj.getStat(), obj));
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