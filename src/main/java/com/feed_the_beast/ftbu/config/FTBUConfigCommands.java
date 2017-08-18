package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.events.registry.RegisterConfigEvent;
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
	public static final PropertyBool LOADED_CHUNKS = new PropertyBool(true);
	public static final PropertyBool TRASH_CAN = new PropertyBool(true);
	public static final PropertyBool CHUNKS = new PropertyBool(true);
	public static final PropertyBool KICKME = new PropertyBool(true);
	public static final PropertyBool RANKS = new PropertyBool(true);
	public static final PropertyBool VIEW_CRASH = new PropertyBool(true);
	public static final PropertyBool HEAL = new PropertyBool(true);
	public static final PropertyBool SET_HOUR = new PropertyBool(true);
	public static final PropertyBool KILLALL = new PropertyBool(true);

	public static void init(RegisterConfigEvent event)
	{
		String id = FTBUFinals.MOD_ID + ".commands";
		event.register(id, "warp", WARP);
		event.register(id, "home", HOME);
		event.register(id, "back", BACK);
		event.register(id, "spawn", SPAWN);
		event.register(id, "inv", INV);
		event.register(id, "tpl", TPL);
		event.register(id, "server_info", SERVER_INFO);
		event.register(id, "loaded_chunks", LOADED_CHUNKS);
		event.register(id, "trash_can", TRASH_CAN);
		event.register(id, "chunks", CHUNKS);
		event.register(id, "kickme", KICKME);
		event.register(id, "ranks", RANKS);
		event.register(id, "view_crash", VIEW_CRASH);
		event.register(id, "heal", HEAL);
		event.register(id, "set_hour", SET_HOUR);
		event.register(id, "killall", KILLALL);
	}
}