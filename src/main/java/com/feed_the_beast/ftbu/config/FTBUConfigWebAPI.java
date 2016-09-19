package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.api_impl.config.PropertyBool;
import com.feed_the_beast.ftbl.api_impl.config.PropertyInt;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraftforge.common.util.Constants;

public class FTBUConfigWebAPI
{
    @ConfigValue(id = "webapi.enabled", file = FTBUFinals.MOD_ID)
    public static final PropertyBool ENABLED = new PropertyBool(false);

    @ConfigValue(id = "webapi.port", file = FTBUFinals.MOD_ID, info = "Port for the WebAPI server")
    public static final PropertyInt PORT = new PropertyInt(Constants.NBT.TAG_SHORT, 4509).setMin(1000).setMax(65535);

    @ConfigValue(id = "webapi.autostart", file = FTBUFinals.MOD_ID, info = "Starts server automatically with world")
    public static final PropertyBool AUTOSTART = new PropertyBool(true);

    @ConfigValue(id = "webapi.output_map", file = FTBUFinals.MOD_ID, info = "true - map, false - table")
    public static final PropertyBool OUTPUT_MAP = new PropertyBool(false);
}