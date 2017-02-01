package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.lib.FinalIDObject;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 10.11.2016.
 */
public class NodeEntry extends FinalIDObject
{
    private DefaultPermissionLevel level;
    private String desc;

    public NodeEntry(String n, DefaultPermissionLevel l, @Nullable String d)
    {
        super(n, LMStringUtils.FLAG_ID_FIX | LMStringUtils.FLAG_ID_ONLY_LOWERCASE);
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