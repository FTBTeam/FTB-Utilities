package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.FTBLibStats;
import com.feed_the_beast.ftbl.api.LangKey;
import com.feed_the_beast.ftbl.api.gui.GuiLang;
import com.feed_the_beast.ftbu.api.TopRegistry;
import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Created by LatvianModder on 28.06.2016.
 */
public class FTBUTops
{
    public static final LangKey LANG_TOP_TITLE = new LangKey("ftbu.top.title");

    public static final StatBase DEATHS_PER_HOUR = (new StatBasic("ftbu.stat.dph", new TextComponentTranslation("ftbu.stat.dph")));

    public static void init()
    {
        TopRegistry.register(StatList.DEATHS, (o1, o2) -> Integer.compare(o2.stats().readStat(StatList.DEATHS), o1.stats().readStat(StatList.DEATHS)), player -> Integer.toString(player.stats().readStat(StatList.DEATHS)));
        TopRegistry.register(StatList.MOB_KILLS, (o1, o2) -> Integer.compare(o2.stats().readStat(StatList.MOB_KILLS), o1.stats().readStat(StatList.MOB_KILLS)), player -> Integer.toString(player.stats().readStat(StatList.MOB_KILLS)));
        TopRegistry.register(DEATHS_PER_HOUR, (o1, o2) -> Double.compare(FTBLibStats.getDeathsPerHour(o2.stats()), FTBLibStats.getDeathsPerHour(o1.stats())), player -> MathHelperLM.toSmallDouble(FTBLibStats.getDeathsPerHour(player.stats())));

        TopRegistry.register(FTBLibStats.LAST_SEEN, (o1, o2) -> Long.compare(FTBLibStats.getLastSeen(o2.stats(), o2.isOnline()), FTBLibStats.getLastSeen(o1.stats(), o1.isOnline())), player ->
        {
            if(player.isOnline())
            {
                return GuiLang.label_online.textComponent();
            }

            return LMStringUtils.getTimeString(System.currentTimeMillis() - FTBLibStats.getLastSeen(player.stats(), player.isOnline()));
        });

        TopRegistry.register(StatList.PLAY_ONE_MINUTE, (o1, o2) -> Long.compare(o2.stats().readStat(StatList.PLAY_ONE_MINUTE), o1.stats().readStat(StatList.PLAY_ONE_MINUTE)), player -> LMStringUtils.getTimeString(player.stats().readStat(StatList.PLAY_ONE_MINUTE)) + " [" + (player.stats().readStat(StatList.PLAY_ONE_MINUTE) / 72000L) + "h]");

        TopRegistry.registerCustomName(StatList.PLAY_ONE_MINUTE, FTBLibStats.TIME_PLAYED_LANG.textComponent());
    }
}
