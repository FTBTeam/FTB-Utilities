package com.feed_the_beast.ftbu.gui.guide;

import com.latmod.lib.EnumNameMap;

/**
 * Created by PC on 17.07.2016.
 */
public enum GuideType
{
    MOD("mod"),
    MODPACK("modpack"),
    MODDING_TUTORIAL("modding_tutorial"),
    OTHER("other");

    private static final EnumNameMap<GuideType> NAME_MAP = new EnumNameMap<>(false, values());

    public final String group;

    GuideType(String s)
    {
        group = s;
    }

    public static GuideType getFromString(String s)
    {
        GuideType type = NAME_MAP.get(s);
        return (type == null) ? OTHER : type;
    }
}