package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryString;

public class FTBUConfigCmd
{
	public static final ConfigEntryString name_admin = new ConfigEntryString("name_admin", "admin");
	public static final ConfigEntryBool back = new ConfigEntryBool("back", true);
	public static final ConfigEntryBool home = new ConfigEntryBool("home", true);
	public static final ConfigEntryBool spawn = new ConfigEntryBool("spawn", true);
	public static final ConfigEntryString name_tplast = new ConfigEntryString("name_tplast", "tpl");
	public static final ConfigEntryBool warp = new ConfigEntryBool("warp", true);
	public static final ConfigEntryBool trash_can = new ConfigEntryBool("trash_can", true);
}