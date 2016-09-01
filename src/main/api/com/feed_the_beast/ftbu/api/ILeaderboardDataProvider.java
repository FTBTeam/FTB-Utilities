package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.IForgePlayer;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface ILeaderboardDataProvider
{
    Object getData(IForgePlayer player);
}