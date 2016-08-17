package com.feed_the_beast.ftbu.gui.guide;

/**
 * Created by PC on 17.07.2016.
 */
public enum GuideType
{
    MODS("mods"),
    MODPACKS("modpacks"),
    MODDING_TUTORIALS("modding_tutorials"),
    OTHER("other");

    public final String group;

    GuideType(String s)
    {
        group = s;
    }

    public static GuideType getFromString(String s)
    {
        switch(s.toLowerCase())
        {
            case "mods":
                return MODS;
            case "modpacks":
                return MODPACKS;
            case "modding_tutorials":
                return MODDING_TUTORIALS;
            default:
                return OTHER;
        }
    }
}