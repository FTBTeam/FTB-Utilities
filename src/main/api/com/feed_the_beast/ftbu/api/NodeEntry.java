package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.lib.FinalIDObject;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class NodeEntry extends FinalIDObject
{
    private DefaultPermissionLevel level;
    private String desc;

    public NodeEntry(String n, DefaultPermissionLevel l, @Nullable String d)
    {
        super(n, StringUtils.FLAG_ID_FIX | StringUtils.FLAG_ID_ONLY_LOWERCASE);
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
}