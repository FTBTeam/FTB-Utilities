package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryDouble;
import com.latmod.lib.annotations.Info;
import com.latmod.lib.annotations.NumberBounds;

public class FTBUConfigGeneral
{
    public static final ConfigEntryBool auto_restart = new ConfigEntryBool(true);

    @NumberBounds(min = 0, max = 720)
    @Info({"Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "24 - 1 Day", "168 - 1 Week", "720 - 1 Month"})
    public static final ConfigEntryDouble restart_timer = new ConfigEntryDouble(0D);

    //public static final ConfigEntryBool ranks_enabled = new ConfigEntryBool(false);
    //public static final ConfigEntryBool ranks_override_chat = new ConfigEntryBool(true);

    //public static final ConfigEntryBool ranks_override_commands = new ConfigEntryBool(true);

    public static final ConfigEntryBool server_info_difficulty = new ConfigEntryBool(true);
    public static final ConfigEntryBool server_info_mode = new ConfigEntryBool(true);
}