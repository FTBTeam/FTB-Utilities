package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.IForgePlayer;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 23.11.2016.
 */
public interface ILeaderboardData
{
    @Nullable
    Object getData(IForgePlayer player);
}