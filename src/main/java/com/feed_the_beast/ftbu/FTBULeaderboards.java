package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.LangKey;
import com.feed_the_beast.ftbl.lib.internal.FTBLibStats;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Created by LatvianModder on 28.06.2016.
 */
public class FTBULeaderboards
{
    public static final LangKey LANG_LEADERBOARD_TITLE = new LangKey("ftbu.leaderboard.title");
    private static final StatBase STAT_DEATHS_PER_HOUR = (new StatBasic("ftbu.stat.dph", new TextComponentTranslation("ftbu.stat.dph")));

    public static void addLeaderboards(IFTBUtilitiesRegistry reg)
    {
        reg.addLeaderboard(StatList.DEATHS, (o1, o2) -> Integer.compare(o2.stats().readStat(StatList.DEATHS), o1.stats().readStat(StatList.DEATHS)), player -> Integer.toString(player.stats().readStat(StatList.DEATHS)));
        reg.addLeaderboard(StatList.MOB_KILLS, (o1, o2) -> Integer.compare(o2.stats().readStat(StatList.MOB_KILLS), o1.stats().readStat(StatList.MOB_KILLS)), player -> Integer.toString(player.stats().readStat(StatList.MOB_KILLS)));
        reg.addLeaderboard(STAT_DEATHS_PER_HOUR, (o1, o2) -> Double.compare(FTBLibStats.getDeathsPerHour(o2.stats()), FTBLibStats.getDeathsPerHour(o1.stats())), player -> MathUtils.toSmallDouble(FTBLibStats.getDeathsPerHour(player.stats())));
        reg.addLeaderboard(StatList.PLAY_ONE_MINUTE, (o1, o2) -> Integer.compare(o2.stats().readStat(StatList.PLAY_ONE_MINUTE), o1.stats().readStat(StatList.PLAY_ONE_MINUTE)), player -> MathUtils.toSmallDouble(player.stats().readStat(StatList.PLAY_ONE_MINUTE) / 72000D) + "h", FTBLibStats.TIME_PLAYED_LANG.textComponent());
        reg.addLeaderboard(FTBLibStats.LAST_SEEN, (o1, o2) -> Long.compare(FTBLibStats.getLastSeen(o2.stats(), o2.isOnline()), FTBLibStats.getLastSeen(o1.stats(), o1.isOnline())), player -> FTBLibStats.getLastSeenTimeString(player.stats(), player.isOnline()));
    }
}
