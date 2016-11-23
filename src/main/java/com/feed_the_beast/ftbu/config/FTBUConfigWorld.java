package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyEntityClassList;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigWorld
{
    public static final PropertyBool CHUNK_CLAIMING = new PropertyBool(true);
    public static final PropertyBool CHUNK_LOADING = new PropertyBool(true);
    public static final PropertyBool SAFE_SPAWN = new PropertyBool(false);
    public static final PropertyEntityClassList BLOCKED_ENTITIES = new PropertyEntityClassList();
    public static final PropertyBool SPAWN_AREA_IN_SP = new PropertyBool(false);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfig(FTBUFinals.MOD_ID, "world.chunk_claiming", CHUNK_CLAIMING);
        reg.addConfig(FTBUFinals.MOD_ID, "world.chunk_loading", CHUNK_LOADING);
        reg.addConfig(FTBUFinals.MOD_ID, "world.safe_spawn", SAFE_SPAWN).setInfo("If set to true, explosions and hostile mobs in spawn area will be disabled, players won't be able to attack each other in spawn area");
        reg.addConfig(FTBUFinals.MOD_ID, "world.blocked_entities", BLOCKED_ENTITIES).setInfo("Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed");
        reg.addConfig(FTBUFinals.MOD_ID, "world.spawn_area_in_sp", SPAWN_AREA_IN_SP).setInfo("Enable spawn area in singleplayer");
    }
}