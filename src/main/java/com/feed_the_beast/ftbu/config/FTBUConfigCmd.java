package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryString;

public class FTBUConfigCmd
{
    public static final ConfigEntryBool back = new ConfigEntryBool(true);
    public static final ConfigEntryBool home = new ConfigEntryBool(true);
    public static final ConfigEntryBool spawn = new ConfigEntryBool(true);
    public static final ConfigEntryString name_tplast = new ConfigEntryString("tpl");
    public static final ConfigEntryBool warp = new ConfigEntryBool(true);
    public static final ConfigEntryBool trash_can = new ConfigEntryBool(true);
}