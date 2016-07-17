package com.feed_the_beast.ftbu.gui.guide;

/**
 * Created by PC on 17.07.2016.
 */
public enum GuideType
{
    MODPACK("modpack"),
    MOD("mod"),
    OTHER("other");

    public final String group;

    GuideType(String s)
    {
        group = s;
    }
}