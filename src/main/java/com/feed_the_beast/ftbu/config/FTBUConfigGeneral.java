package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.impl.PropertyBool;
import com.feed_the_beast.ftbl.api.config.impl.PropertyDouble;
import com.latmod.lib.annotations.Info;

public class FTBUConfigGeneral
{
    public static final PropertyBool AUTO_RESTART = new PropertyBool(true);

    @Info({"Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "24 - 1 Day", "168 - 1 Week", "720 - 1 Month"})
    public static final PropertyDouble RESTART_TIMER = new PropertyDouble(0D).setMin(0D).setMax(720);

    //public static final PropertyBool RANKS_ENABLED = new PropertyBool(false);
    //public static final PropertyBool RANKS_OVERRIDE_CHAT = new PropertyBool(true);

    //public static final PropertyBool RANKS_OVERRIDE_COMMANDS = new PropertyBool(true);

    public static final PropertyBool SERVER_INFO_DIFFICULTY = new PropertyBool(true);
    public static final PropertyBool SERVER_INFO_MODE = new PropertyBool(true);
}