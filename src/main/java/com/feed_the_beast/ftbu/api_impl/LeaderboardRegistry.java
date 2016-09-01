package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbu.api.ILeaderboardDataProvider;
import com.feed_the_beast.ftbu.api.ILeaderboardRegistry;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by LatvianModder on 04.07.2016.
 */
enum LeaderboardRegistry implements ILeaderboardRegistry
{
    INSTANCE;

    private final Map<StatBase, Comparator<IForgePlayer>> TOP_COMPARATOR_REGISTRY = new HashMap<>();
    private final Map<StatBase, ILeaderboardDataProvider> TOP_DATA_REGISTRY = new HashMap<>();
    private final Map<StatBase, ITextComponent> TOP_NAME_REGISTRY = new HashMap<>();

    @Override
    public void register(StatBase stat, @Nullable Comparator<IForgePlayer> comparator, @Nullable ILeaderboardDataProvider data)
    {
        if(comparator != null)
        {
            TOP_COMPARATOR_REGISTRY.put(stat, comparator);
        }

        if(data != null)
        {
            TOP_DATA_REGISTRY.put(stat, data);
        }
    }

    @Override
    public void registerCustomName(StatBase stat, ITextComponent component)
    {
        TOP_NAME_REGISTRY.put(stat, component);
    }

    @Override
    public Set<StatBase> getRegistred()
    {
        return TOP_DATA_REGISTRY.keySet();
    }

    @Override
    @Nullable
    public Comparator<IForgePlayer> getComparator(StatBase stat)
    {
        return TOP_COMPARATOR_REGISTRY.get(stat);
    }

    @Override
    @Nullable
    public ILeaderboardDataProvider getDataProvider(StatBase stat)
    {
        return TOP_DATA_REGISTRY.get(stat);
    }

    @Override
    public ITextComponent getName(StatBase stat)
    {
        //FIXME: Gray stat name
        ITextComponent c = TOP_NAME_REGISTRY.containsKey(stat) ? TOP_NAME_REGISTRY.get(stat) : new TextComponentTranslation(stat.statId);
        c = c.createCopy();
        c.getStyle().setColor(null);
        return c;
    }
}