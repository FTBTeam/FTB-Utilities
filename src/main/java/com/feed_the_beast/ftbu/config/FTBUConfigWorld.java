package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyEntityClassList;
import com.feed_the_beast.ftbl.lib.config.PropertyShort;
import com.feed_the_beast.ftbu.FTBUFinals;

import java.util.ArrayList;

public class FTBUConfigWorld
{
    @ConfigValue(id = "world.chunk_claiming", file = FTBUFinals.MOD_ID)
    public static final PropertyBool CHUNK_CLAIMING = new PropertyBool(true);

    @ConfigValue(id = "world.chunk_loading", file = FTBUFinals.MOD_ID)
    public static final PropertyBool CHUNK_LOADING = new PropertyBool(true);

    @ConfigValue(id = "world.safe_spawn", file = FTBUFinals.MOD_ID, info = "If set to true, explosions and hostile mobs in spawn area will be disabled, players won't be able to attack each other in spawn area")
    public static final PropertyBool SAFE_SPAWN = new PropertyBool(false);

    @ConfigValue(id = "world.blocked_entities", file = FTBUFinals.MOD_ID, info = "Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed")
    public static final PropertyEntityClassList BLOCKED_ENTITIES = new PropertyEntityClassList(new ArrayList<>());

    @ConfigValue(id = "world.spawn_area_in_sp", file = FTBUFinals.MOD_ID, info = "Enable spawn area in singleplayer")
    public static final PropertyBool SPAWN_AREA_IN_SP = new PropertyBool(false);

    @ConfigValue(id = "world.temp.max_claimed_chunks", file = FTBUFinals.MOD_ID, info = "Temp config for max chunks that player can claim")
    public static final PropertyShort MAX_CLAIMED_CHUNKS = new PropertyShort(1000, 0, 30000);

    @ConfigValue(id = "world.temp.max_loaded_chunks", file = FTBUFinals.MOD_ID, info = "Temp config for max chunks that player can load")
    public static final PropertyShort MAX_LOADED_CHUNKS = new PropertyShort(64, 0, 30000);

    @ConfigValue(id = "world.locked_in_claimed_chunks", file = FTBUFinals.MOD_ID, info = "Players are allowed to interact only in their claimed chunks.")
    public static final PropertyBool LOCKED_IN_CLAIMED_CHUNKS = new PropertyBool(false);
}