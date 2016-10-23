package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigCommands
{
    @ConfigValue(id = "commands.warp", file = FTBUFinals.MOD_ID)
    public static final PropertyBool WARP = new PropertyBool(true);

    @ConfigValue(id = "commands.home", file = FTBUFinals.MOD_ID)
    public static final PropertyBool HOME = new PropertyBool(true);

    @ConfigValue(id = "commands.back", file = FTBUFinals.MOD_ID)
    public static final PropertyBool BACK = new PropertyBool(true);

    @ConfigValue(id = "commands.spawn", file = FTBUFinals.MOD_ID)
    public static final PropertyBool SPAWN = new PropertyBool(true);

    @ConfigValue(id = "commands.inv", file = FTBUFinals.MOD_ID)
    public static final PropertyBool INV = new PropertyBool(true);

    @ConfigValue(id = "commands.tpl", file = FTBUFinals.MOD_ID)
    public static final PropertyBool TPL = new PropertyBool(true);

    @ConfigValue(id = "commands.server_info", file = FTBUFinals.MOD_ID)
    public static final PropertyBool SERVER_INFO = new PropertyBool(true);

    @ConfigValue(id = "commands.trash_can", file = FTBUFinals.MOD_ID)
    public static final PropertyBool TRASH_CAN = new PropertyBool(true);

    @ConfigValue(id = "commands.chunks", file = FTBUFinals.MOD_ID)
    public static final PropertyBool CHUNKS = new PropertyBool(true);
}