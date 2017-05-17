package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.IForgePlayer;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface ILeaderboardData
{
    @Nullable
    Object getData(IForgePlayer player);
}