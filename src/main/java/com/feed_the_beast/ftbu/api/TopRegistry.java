package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by LatvianModder on 04.07.2016.
 */
@ParametersAreNonnullByDefault
public class TopRegistry
{
    private static final Map<StatBase, Comparator<IForgePlayer>> COMPARATOR_REGISTRY = new HashMap<>();
    private static final Map<StatBase, DataSupplier> DATA_REGISTRY = new HashMap<>();
    private static final Map<StatBase, ITextComponent> NAME_REGISTRY = new HashMap<>();

    public interface DataSupplier
    {
        @Nonnull
        Object getData(@Nonnull IForgePlayer player);
    }

    public static void register(StatBase stat, @Nullable Comparator<IForgePlayer> comparator, @Nullable DataSupplier data)
    {
        if(comparator != null)
        {
            COMPARATOR_REGISTRY.put(stat, comparator);
        }

        if(data != null)
        {
            DATA_REGISTRY.put(stat, data);
        }
    }

    public static void registerCustomName(StatBase stat, ITextComponent component)
    {
        NAME_REGISTRY.put(stat, component);
    }

    @Nonnull
    public static Set<StatBase> getKeys()
    {
        return DATA_REGISTRY.keySet();
    }

    @Nullable
    public static Comparator<IForgePlayer> getComparator(StatBase stat)
    {
        return COMPARATOR_REGISTRY.get(stat);
    }

    @Nullable
    public static DataSupplier getDataSuppier(StatBase stat)
    {
        return DATA_REGISTRY.get(stat);
    }

    @Nonnull
    public ITextComponent getName(StatBase stat)
    {
        //FIXME: Gray stat name
        ITextComponent c = NAME_REGISTRY.containsKey(stat) ? NAME_REGISTRY.get(stat) : new TextComponentTranslation(stat.statId);
        c = c.createCopy();
        c.getStyle().setColor(null);
        return c;
    }
}