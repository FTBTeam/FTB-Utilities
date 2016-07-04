package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.FTBLibStats;
import com.feed_the_beast.ftbl.api.LangKey;
import com.feed_the_beast.ftbl.api.client.gui.GuiLang;
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
        TopRegistry.register(DEATHS_PER_HOUR, (o1, o2) -> Double.compare(FTBLibStats.getDeathsPerHour(o2.stats()), FTBLibStats.getDeathsPerHour(o1.stats())), player -> MathHelperLM.toSmallDouble(FTBLibStats.getDeathsPerHour(player.stats())));

        TopRegistry.register(FTBLibStats.LAST_SEEN, (o1, o2) -> Long.compare(o2.stats().readStat(FTBLibStats.LAST_SEEN), o1.stats().readStat(FTBLibStats.LAST_SEEN)), player ->
        {
            if(player.isOnline())
            {
                return GuiLang.label_online.textComponent();
            }

            return LMStringUtils.getTimeString(System.currentTimeMillis() - player.stats().readStat(FTBLibStats.LAST_SEEN));
        });

        TopRegistry.register(StatList.PLAY_ONE_MINUTE, (o1, o2) -> Long.compare(o2.stats().readStat(StatList.PLAY_ONE_MINUTE), o1.stats().readStat(StatList.PLAY_ONE_MINUTE)), player -> LMStringUtils.getTimeString(player.stats().readStat(StatList.PLAY_ONE_MINUTE)) + " [" + (player.stats().readStat(StatList.PLAY_ONE_MINUTE) / 72000L) + "h]");
    }
}
