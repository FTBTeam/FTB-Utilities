package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.lib.FinalIDObject;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by LatvianModder on 10.11.2016.
 */
public class NodeEntry extends FinalIDObject
{
    private static final Map<DefaultPermissionLevel, String> COLOR_MAP = new EnumMap<>(DefaultPermissionLevel.class);

    static
    {
        COLOR_MAP.put(DefaultPermissionLevel.ALL, "#72FF85");
        COLOR_MAP.put(DefaultPermissionLevel.OP, "#42A3FF");
        COLOR_MAP.put(DefaultPermissionLevel.NONE, "#FF4242");
    }

    private DefaultPermissionLevel level;
    private String desc;

    public NodeEntry(String n, DefaultPermissionLevel l, @Nullable String d)
    {
        super(n);
        level = l;
        desc = d;
    }

    public DefaultPermissionLevel getLevel()
    {
        return level;
    }

    @Nullable
    public String getDescription()
    {
        return desc;
    }

    public String getColor()
    {
        return COLOR_MAP.get(getLevel());
    }
}