package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.LangKey;
import com.feed_the_beast.ftbl.api.gui.GuiLang;
import latmod.lib.LMStringUtils;
import latmod.lib.MathHelperLM;
import latmod.lib.util.FinalIDObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class Top extends FinalIDObject implements Comparator<ForgePlayerMP>
{
    public static final LangKey langTopTitle = new LangKey("ftbu.top.title");
    static final Map<String, Top> registry = new HashMap<>();
    public final LangKey langKey;

    public Top(String s)
    {
        super(s);
        langKey = new LangKey("ftbu.top." + getID());
    }

    public static void init()
    {
        add(new Top("first_joined")
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

        add(new Top("deaths")
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

        add(new Top("deaths_per_hour")
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

        add(new Top("last_seen")
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

        add(new Top("time_played")
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

    public static void add(Top t)
    {
        registry.put(t.getID(), t);
    }

    public abstract Object getData(ForgePlayerMP p);
}