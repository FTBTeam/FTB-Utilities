package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigWebAPI
{
    public static final PropertyBool ENABLED = new PropertyBool(false);
    public static final PropertyString FILE_LOCATION = new PropertyString("");
    public static final PropertyBool OUTPUT_MAP = new PropertyBool(false);
    public static final PropertyInt UPDATE_INTERVAL = new PropertyInt(5);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfig(FTBUFinals.MOD_ID, "webapi.enabled", ENABLED);
        reg.addConfig(FTBUFinals.MOD_ID, "webapi.file_location", FILE_LOCATION);
        reg.addConfig(FTBUFinals.MOD_ID, "webapi.output_map", OUTPUT_MAP).setInfo("true - Map", "false - Table");
        reg.addConfig(FTBUFinals.MOD_ID, "webapi.update_interval", UPDATE_INTERVAL).setInfo("Update interval in minutes");
    }
}