package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyEntityClassList;
import com.feed_the_beast.ftbl.lib.config.PropertyIntList;
import com.feed_the_beast.ftbl.lib.config.PropertyShort;
import com.feed_the_beast.ftbu.FTBUFinals;

import java.util.Collections;

public class FTBUConfigWorld
{
    public static final PropertyBool CHUNK_CLAIMING = new PropertyBool(true);
    public static final PropertyBool CHUNK_LOADING = new PropertyBool(true);
    public static final PropertyBool SAFE_SPAWN = new PropertyBool(false);
    public static final PropertyEntityClassList BLOCKED_ENTITIES = new PropertyEntityClassList(Collections.emptyList());
    public static final PropertyBool SPAWN_AREA_IN_SP = new PropertyBool(false);
    public static final PropertyShort MAX_CLAIMED_CHUNKS = new PropertyShort(1000, 0, 30000);
    public static final PropertyShort MAX_LOADED_CHUNKS = new PropertyShort(64, 0, 30000);
    public static final PropertyBool LOCKED_IN_CLAIMED_CHUNKS = new PropertyBool(false);
    public static final PropertyIntList LOCKED_IN_DIMENSIONS = new PropertyIntList(1, 0, -1);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfig(FTBUFinals.MOD_ID, "world.chunk_claiming", CHUNK_CLAIMING);
        reg.addConfig(FTBUFinals.MOD_ID, "world.chunk_loading", CHUNK_LOADING);
        reg.addConfig(FTBUFinals.MOD_ID, "world.safe_spawn", SAFE_SPAWN).setInfo("If set to true, explosions and hostile mobs in spawn area will be disabled, players won't be able to attack each other in spawn area");
        reg.addConfig(FTBUFinals.MOD_ID, "world.blocked_entities", BLOCKED_ENTITIES).setInfo("Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed");
        reg.addConfig(FTBUFinals.MOD_ID, "world.spawn_area_in_sp", SPAWN_AREA_IN_SP).setInfo("Enable spawn area in singleplayer");
        reg.addConfig(FTBUFinals.MOD_ID, "world.max_claimed_chunks", MAX_CLAIMED_CHUNKS).setInfo("Temp config for max chunks that player can claim");
        reg.addConfig(FTBUFinals.MOD_ID, "world.max_loaded_chunks", MAX_LOADED_CHUNKS).setInfo("Temp config for max chunks that player can load");
        reg.addConfig(FTBUFinals.MOD_ID, "world.locked_in_claimed_chunks", LOCKED_IN_CLAIMED_CHUNKS).setInfo("Players are allowed to interact only in their claimed chunks");
        reg.addConfig(FTBUFinals.MOD_ID, "world.locked_in_dimensions", LOCKED_IN_DIMENSIONS).setInfo("Specifies dimensions for world.locked_in_claimed_chunks");
    }
}