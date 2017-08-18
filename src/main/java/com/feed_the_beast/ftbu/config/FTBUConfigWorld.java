package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.events.registry.RegisterConfigEvent;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigWorld
{
	public static final PropertyBool CHUNK_CLAIMING = new PropertyBool(true);
	public static final PropertyBool CHUNK_LOADING = new PropertyBool(true);
	public static final PropertyBool SAFE_SPAWN = new PropertyBool(false);
	public static final PropertyBool SPAWN_AREA_IN_SP = new PropertyBool(false);
	public static final PropertyBool LOG_CHUNKLOADING = new PropertyBool(false);

	public static void init(RegisterConfigEvent event)
	{
		String id = FTBUFinals.MOD_ID + ".world";
		event.register(id, "chunk_claiming", CHUNK_CLAIMING);
		event.register(id, "chunk_loading", CHUNK_LOADING);
		event.register(id, "safe_spawn", SAFE_SPAWN);
		event.register(id, "spawn_area_in_sp", SPAWN_AREA_IN_SP);
		event.register(id, "log_chunkloading", LOG_CHUNKLOADING);
	}
}