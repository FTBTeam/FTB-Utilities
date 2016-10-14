package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IRegistryObject;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public abstract class Leaderboard implements IRegistryObject
{
    private final StatBase stat;
    private final Comparator<IForgePlayer> comparator;

    public Leaderboard(StatBase s, @Nullable Comparator<IForgePlayer> c)
    {
        stat = s;
        comparator = c;
    }

    public final StatBase getStat()
    {
        return stat;
    }

    @Nullable
    public abstract Object getData(IForgePlayer player);

    @Nullable
    public Comparator<IForgePlayer> getComparator()
    {
        return comparator;
    }

    public ITextComponent getName()
    {
        ITextComponent c = new TextComponentTranslation(getStat().statId);
        c = c.createCopy();
        c.getStyle().setColor(null);
        return c;
    }
}
