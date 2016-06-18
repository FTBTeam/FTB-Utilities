package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryInt;
import com.latmod.lib.annotations.Info;
import com.latmod.lib.annotations.NumberBounds;

public class FTBUConfigWebAPI
{
    @NumberBounds(min = 1000, max = 65535)
    @Info("Port for the WebAPI server")
    public static final ConfigEntryInt port = new ConfigEntryInt(4509);

    @Info("Starts server automatically with world")
    public static final ConfigEntryBool autostart = new ConfigEntryBool(true);

    @Info("true - map, false - table")
    public static final ConfigEntryBool output_map = new ConfigEntryBool(false);
}