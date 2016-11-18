package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbu.api.IFTBUtilitiesPlugin;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import com.feed_the_beast.ftbu.api.Leaderboard;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import net.minecraft.stats.StatBase;

import java.util.HashMap;
import java.util.Map;

public class FTBUCommon implements IFTBUtilitiesRegistry // FTBUClient
{
    public final Map<StatBase, Leaderboard> leaderboards = new HashMap<>();

    public void preInit()
    {
        addLeaderboard(FTBULeaderboards.DEATHS);
        addLeaderboard(FTBULeaderboards.MOB_KILLS);
        addLeaderboard(FTBULeaderboards.DEATHS_PER_HOUR_LB);
        addLeaderboard(FTBULeaderboards.PLAY_ONE_MINUTE);
        addLeaderboard(FTBULeaderboards.LAST_SEEN);

        for(IFTBUtilitiesPlugin p : FTBUtilitiesAPI_Impl.INSTANCE.getAllPlugins())
        {
            p.registerCommon(this);
        }
    }

    public void postInit()
    {
    }

    public void onReloadedClient()
    {
    }

    @Override
    public void addLeaderboard(Leaderboard leaderboard)
    {
        leaderboards.put(leaderboard.getStat(), leaderboard);
    }
}