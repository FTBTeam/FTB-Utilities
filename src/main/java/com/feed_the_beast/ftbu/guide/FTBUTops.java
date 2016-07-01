package com.feed_the_beast.ftbu.guide;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.client.gui.GuiLang;
import com.feed_the_beast.ftbu.api.Top;
import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMStringUtils;

/**
 * Created by LatvianModder on 28.06.2016.
 */
public class FTBUTops
{
    public static void init()
    {
        Top.register(new Top("first_joined")
        {
            @Override
            public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
            {
                return Long.compare(o1.stats.firstJoined, o2.stats.firstJoined);
            }

            @Override
            public Object getData(ForgePlayerMP p)
            {
                return LMStringUtils.getTimeString(System.currentTimeMillis() - p.stats.firstJoined);
            }
        });

        Top.register(new Top("deaths")
        {
            @Override
            public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
            {
                return Integer.compare(o2.stats.deaths, o1.stats.deaths);
            }

            @Override
            public Object getData(ForgePlayerMP p)
            {
                return Integer.toString(p.stats.deaths);
            }
        });

        Top.register(new Top("deaths_per_hour")
        {
            @Override
            public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
            {
                return Double.compare(o2.stats.getDeathsPerHour(), o1.stats.getDeathsPerHour());
            }

            @Override
            public Object getData(ForgePlayerMP p)
            {
                return MathHelperLM.toSmallDouble(p.stats.getDeathsPerHour());
            }
        });

        Top.register(new Top("last_seen")
        {
            @Override
            public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
            {
                return Long.compare(o2.stats.lastSeen, o1.stats.lastSeen);
            }

            @Override
            public Object getData(ForgePlayerMP p)
            {
                if(p.isOnline())
                {
                    return GuiLang.label_online.textComponent();
                }
                return LMStringUtils.getTimeString(System.currentTimeMillis() - p.stats.lastSeen);
            }
        });

        Top.register(new Top("time_played")
        {
            @Override
            public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
            {
                return Long.compare(o2.stats.timePlayed, o1.stats.timePlayed);
            }

            @Override
            public Object getData(ForgePlayerMP p)
            {
                return LMStringUtils.getTimeString(p.stats.timePlayed) + " [" + (p.stats.timePlayed / 3600000L) + "h]";
            }
        });
    }
}
