package com.feed_the_beast.ftbu.api.leaderboard;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public interface ILeaderboard
{
    StatBase getStat();

    @Nullable
    Object getData(IForgePlayer player);

    @Nullable
    default Comparator<IForgePlayer> getComparator()
    {
        return null;
    }

    default ITextComponent getName()
    {
        ITextComponent c = new TextComponentTranslation(getStat().statId);
        c = c.createCopy();
        c.getStyle().setColor(null);
        return c;
    }
}