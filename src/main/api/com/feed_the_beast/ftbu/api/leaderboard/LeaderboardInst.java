package com.feed_the_beast.ftbu.api.leaderboard;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import net.minecraft.stats.StatBase;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public abstract class LeaderboardInst implements ILeaderboard
{
    private StatBase stat;
    private Comparator<IForgePlayer> comparator;

    public LeaderboardInst(StatBase s, @Nullable Comparator<IForgePlayer> c)
    {
        stat = s;
        comparator = c;
    }

    @Override
    @Nullable
    public Comparator<IForgePlayer> getComparator()
    {
        return comparator;
    }

    @Override
    public StatBase getStat()
    {
        return stat;
    }
}
