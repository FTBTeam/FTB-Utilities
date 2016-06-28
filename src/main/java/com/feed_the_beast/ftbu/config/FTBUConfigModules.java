package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;

public class FTBUConfigModules
{
    public static final ConfigEntryBool backups = new ConfigEntryBool(true);
    public static final ConfigEntryBool auto_restart = new ConfigEntryBool(true);
    public static final ConfigEntryBool chunk_claiming = new ConfigEntryBool(true);
    public static final ConfigEntryBool chunk_loading = new ConfigEntryBool(true);
    public static final ConfigEntryBool motd = new ConfigEntryBool(true);
    public static final ConfigEntryBool starting_items = new ConfigEntryBool(true);
    public static final ConfigEntryBool web_api = new ConfigEntryBool(false);
    //public static final ConfigEntryBool starting_items = new ConfigEntryBool("starting_items", true);
}