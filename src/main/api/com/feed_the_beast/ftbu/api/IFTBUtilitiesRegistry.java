package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * Created by LatvianModder on 17.11.2016.
 */
public interface IFTBUtilitiesRegistry
{
    void addLeaderboard(StatBase stat, @Nullable Comparator<IForgePlayer> comparator, ILeaderboardData data, ITextComponent displayName);

    default void addLeaderboard(StatBase stat, @Nullable Comparator<IForgePlayer> comparator, ILeaderboardData data)
    {
        addLeaderboard(stat, comparator, data, new TextComponentTranslation(stat.statId));
    }

    void addCustomPermPrefix(NodeEntry entry);
}