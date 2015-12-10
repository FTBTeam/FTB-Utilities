package latmod.ftbu.mod.config;

import latmod.lib.config.*;

public class FTBUConfigCmd
{
	public static final ConfigGroup group = new ConfigGroup("commands");
	public static final ConfigEntryString name_admin = new ConfigEntryString("name_admin", "admin");
	public static final ConfigEntryString name_back = new ConfigEntryString("name_back", "back");
	public static final ConfigEntryString name_home = new ConfigEntryString("name_home", "home");
	public static final ConfigEntryString name_spawn = new ConfigEntryString("name_spawn", "spawn");
	public static final ConfigEntryString name_tplast = new ConfigEntryString("name_tplast", "tpl");
	public static final ConfigEntryString name_warp = new ConfigEntryString("name_warp", "warp");
}