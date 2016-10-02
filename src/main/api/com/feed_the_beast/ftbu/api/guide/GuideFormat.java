package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.lib.EnumNameMap;

/**
 * Created by PC on 02.10.2016.
 */
public enum GuideFormat
{
    JSON("json"),
    MD("md"),
    UNSUPPORTED("unsupported");

    private static final EnumNameMap<GuideFormat> NAME_MAP = new EnumNameMap<>(values(), false);

    public final String group;

    GuideFormat(String s)
    {
        group = s;
    }

    public static GuideFormat getFromString(String s)
    {
        GuideFormat type = NAME_MAP.get(s);
        return (type == null) ? UNSUPPORTED : type;
    }
}