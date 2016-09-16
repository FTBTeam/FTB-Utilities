package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.impl.PropertyBool;
import com.feed_the_beast.ftbl.api.config.impl.PropertyInt;
import com.latmod.lib.annotations.Info;
import net.minecraftforge.common.util.Constants;

public class FTBUConfigWebAPI
{
    public static final PropertyBool ENABLED = new PropertyBool(false);

    @Info("Port for the WebAPI server")
    public static final PropertyInt PORT = new PropertyInt(Constants.NBT.TAG_SHORT, 4509).setMin(1000).setMax(65535);

    @Info("Starts server automatically with world")
    public static final PropertyBool AUTOSTART = new PropertyBool(true);

    @Info("true - map, false - table")
    public static final PropertyBool OUTPUT_MAP = new PropertyBool(false);
}