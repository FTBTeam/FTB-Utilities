package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigCommands
{
    public static final PropertyBool WARP = new PropertyBool(true);
    public static final PropertyBool HOME = new PropertyBool(true);
    public static final PropertyBool BACK = new PropertyBool(true);
    public static final PropertyBool SPAWN = new PropertyBool(true);
    public static final PropertyBool INV = new PropertyBool(true);
    public static final PropertyBool TPL = new PropertyBool(true);
    public static final PropertyBool SERVER_INFO = new PropertyBool(true);
    public static final PropertyBool TRASH_CAN = new PropertyBool(true);
    public static final PropertyBool CHUNKS = new PropertyBool(true);
    public static final PropertyBool JS = new PropertyBool(false);
    public static final PropertyBool KICKME = new PropertyBool(true);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfig(FTBUFinals.MOD_ID, "commands.warp", WARP);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.home", HOME);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.back", BACK);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.spawn", SPAWN);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.inv", INV);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.tpl", TPL);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.server_info", SERVER_INFO);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.trash_can", TRASH_CAN);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.chunks", CHUNKS);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.js", JS);
        reg.addConfig(FTBUFinals.MOD_ID, "commands.kickme", KICKME);
    }
}