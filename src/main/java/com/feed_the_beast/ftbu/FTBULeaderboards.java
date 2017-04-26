package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.LangKey;
import com.feed_the_beast.ftbl.lib.client.DrawableItem;
import com.feed_the_beast.ftbl.lib.internal.FTBLibStats;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Comparator;

/**
 * Created by LatvianModder on 28.06.2016.
 */
public class FTBULeaderboards
{
    public static final LangKey LANG_LEADERBOARD_TITLE = new LangKey("ftbu.leaderboard.title");
    private static final StatBase STAT_DEATHS_PER_HOUR = (new StatBasic("ftbu.stat.dph", new TextComponentTranslation("ftbu.stat.dph")));

    public static void addLeaderboards(IFTBUtilitiesRegistry reg)
    {
        reg.addLeaderboard(StatList.DEATHS, Comparator.comparingInt(o -> o.stats().readStat(StatList.DEATHS)), player -> Integer.toString(player.stats().readStat(StatList.DEATHS)), new DrawableItem(new ItemStack(Items.BONE)));
        reg.addLeaderboard(StatList.MOB_KILLS, Comparator.comparingInt(o -> o.stats().readStat(StatList.MOB_KILLS)), player -> Integer.toString(player.stats().readStat(StatList.MOB_KILLS)), new DrawableItem(new ItemStack(Items.IRON_SWORD)));
        reg.addLeaderboard(STAT_DEATHS_PER_HOUR, Comparator.comparingDouble(o -> FTBLibStats.getDeathsPerHour(o.stats())), player -> MathUtils.toSmallDouble(FTBLibStats.getDeathsPerHour(player.stats())), new DrawableItem(new ItemStack(Items.ELYTRA)));
        reg.addLeaderboard(StatList.PLAY_ONE_MINUTE, Comparator.comparingInt(o -> o.stats().readStat(StatList.PLAY_ONE_MINUTE)), player -> MathUtils.toSmallDouble(player.stats().readStat(StatList.PLAY_ONE_MINUTE) / 72000D) + "h", FTBLibStats.TIME_PLAYED_LANG.textComponent(), new DrawableItem(new ItemStack(Items.CLOCK)));
        reg.addLeaderboard(FTBLibStats.LAST_SEEN, Comparator.comparingLong(o -> FTBLibStats.getLastSeen(o.stats(), o.isOnline())), player -> FTBLibStats.getLastSeenTimeString(player.stats(), player.isOnline()), new DrawableItem(new ItemStack(Items.BED)));
    }
}