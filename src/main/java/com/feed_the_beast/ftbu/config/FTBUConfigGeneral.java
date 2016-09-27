package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigFileProvider;
import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.api.config.IConfigFileProvider;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.latmod.lib.config.PropertyBool;
import com.latmod.lib.config.PropertyDouble;
import com.latmod.lib.util.LMUtils;

import java.io.File;

public class FTBUConfigGeneral
{
    @ConfigFileProvider(FTBUFinals.MOD_ID)
    public static final IConfigFileProvider FILE = () -> new File(LMUtils.folderLocal, "ftbu/config.json");

    @ConfigValue(id = "general.auto_restart", file = FTBUFinals.MOD_ID)
    public static final PropertyBool AUTO_RESTART = new PropertyBool(false);

    @ConfigValue(id = "general.restart_timer", file = FTBUFinals.MOD_ID, info = {"Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "4 - 1 Day", "168 - 1 Week", "720 - 1 Month"})
    public static final PropertyDouble RESTART_TIMER = new PropertyDouble(12D).setMin(0D).setMax(720);

    //public static final PropertyBool RANKS_ENABLED = new PropertyBool(false);
    //public static final PropertyBool RANKS_OVERRIDE_CHAT = new PropertyBool(true);

    //public static final PropertyBool RANKS_OVERRIDE_COMMANDS = new PropertyBool(true);

    @ConfigValue(id = "general.server_info_difficulty", file = FTBUFinals.MOD_ID)
    public static final PropertyBool SERVER_INFO_DIFFICULTY = new PropertyBool(true);

    @ConfigValue(id = "general.server_info_mode", file = FTBUFinals.MOD_ID)
    public static final PropertyBool SERVER_INFO_MODE = new PropertyBool(true);
}