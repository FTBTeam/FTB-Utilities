package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Set;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface ILeaderboardRegistry
{
    void register(StatBase stat, @Nullable Comparator<IForgePlayer> comparator, @Nullable ILeaderboardDataProvider data);

    void registerCustomName(StatBase stat, ITextComponent component);

    Set<StatBase> getRegistred();

    @Nullable
    Comparator<IForgePlayer> getComparator(StatBase stat);

    @Nullable
    ILeaderboardDataProvider getDataProvider(StatBase stat);

    ITextComponent getName(StatBase stat);
}
