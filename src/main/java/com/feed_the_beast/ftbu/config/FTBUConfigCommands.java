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
	public static final PropertyBool LOADED_CHUNKS = new PropertyBool(true);
	public static final PropertyBool TRASH_CAN = new PropertyBool(true);
	public static final PropertyBool CHUNKS = new PropertyBool(true);
	public static final PropertyBool KICKME = new PropertyBool(true);
	public static final PropertyBool RANKS = new PropertyBool(true);
	public static final PropertyBool VIEW_CRASH = new PropertyBool(true);
	public static final PropertyBool HEAL = new PropertyBool(true);
	public static final PropertyBool SET_HOUR = new PropertyBool(true);
	public static final PropertyBool KILLALL = new PropertyBool(true);

	public static void init(IFTBLibRegistry reg)
	{
		String id = FTBUFinals.MOD_ID + ".commands";
		reg.addConfig(id, "warp", WARP);
		reg.addConfig(id, "home", HOME);
		reg.addConfig(id, "back", BACK);
		reg.addConfig(id, "spawn", SPAWN);
		reg.addConfig(id, "inv", INV);
		reg.addConfig(id, "tpl", TPL);
		reg.addConfig(id, "server_info", SERVER_INFO);
		reg.addConfig(id, "loaded_chunks", LOADED_CHUNKS);
		reg.addConfig(id, "trash_can", TRASH_CAN);
		reg.addConfig(id, "chunks", CHUNKS);
		reg.addConfig(id, "kickme", KICKME);
		reg.addConfig(id, "ranks", RANKS);
		reg.addConfig(id, "view_crash", VIEW_CRASH);
		reg.addConfig(id, "heal", HEAL);
		reg.addConfig(id, "set_hour", SET_HOUR);
		reg.addConfig(id, "killall", KILLALL);
	}
}