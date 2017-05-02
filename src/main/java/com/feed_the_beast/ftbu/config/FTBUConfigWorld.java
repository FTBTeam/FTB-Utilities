package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigWorld
{
    public static final PropertyBool CHUNK_CLAIMING = new PropertyBool(true);
    public static final PropertyBool CHUNK_LOADING = new PropertyBool(true);
    public static final PropertyBool SAFE_SPAWN = new PropertyBool(false);
    public static final PropertyBool SPAWN_AREA_IN_SP = new PropertyBool(false);
    public static final PropertyBool LOG_CHUNKLOADING = new PropertyBool(false);

    public static void init(IFTBLibRegistry reg)
    {
        String id = FTBUFinals.MOD_ID + ".world";
        reg.addConfig(id, "chunk_claiming", CHUNK_CLAIMING);
        reg.addConfig(id, "chunk_loading", CHUNK_LOADING);
        reg.addConfig(id, "safe_spawn", SAFE_SPAWN);
        reg.addConfig(id, "spawn_area_in_sp", SPAWN_AREA_IN_SP);
        reg.addConfig(id, "log_chunkloading", LOG_CHUNKLOADING);
    }
}