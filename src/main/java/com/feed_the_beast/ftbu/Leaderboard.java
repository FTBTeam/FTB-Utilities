package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbu.api.ILeaderboardData;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * Created by LatvianModder on 27.09.2016.
 */
class Leaderboard
{
    public final StatBase stat;
    public final Comparator<IForgePlayer> comparator;
    public final ILeaderboardData data;
    public final ITextComponent name;

    public Leaderboard(StatBase s, @Nullable Comparator<IForgePlayer> c, ILeaderboardData d, ITextComponent n)
    {
        stat = s;
        comparator = c;
        data = d;
        name = n;
    }
}