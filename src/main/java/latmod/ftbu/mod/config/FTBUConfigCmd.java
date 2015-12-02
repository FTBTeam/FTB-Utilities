package latmod.ftbu.mod.config;

import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class FTBUConfigCmd
{
	public static final ConfigGroup group = new ConfigGroup("commands");
	public static final ConfigEntryInt maxHomesPlayer = new ConfigEntryInt("maxHomesPlayer", new IntBounds(0, 0, 32000)).setInfo("Max home count for players");
	public static final ConfigEntryInt maxHomesAdmin = new ConfigEntryInt("maxHomesAdmin", new IntBounds(100, 0, 32000)).setInfo("Max home count for admins");
	public static final ConfigEntryBool crossDimHomes = new ConfigEntryBool("crossDimHomes", true).setInfo("Can use /home to teleport to/from another dimension");
	
	public static final ConfigEntryString name_back = new ConfigEntryString("name_back", "back");
	public static final ConfigEntryString name_spawn = new ConfigEntryString("name_spawn", "spawn");
	public static final ConfigEntryString name_tplast = new ConfigEntryString("name_tplast", "tpl");
	public static final ConfigEntryString name_warp = new ConfigEntryString("name_warp", "warp");
	public static final ConfigEntryString name_home = new ConfigEntryString("name_home", "home");
	
	public static final ConfigEntryString name_admin = new ConfigEntryString("name_admin", "admin").setInfo("Set to blank to split each admin command into its own command");
	public static final ConfigEntryString name_backup = new ConfigEntryString("name_backup", "backup");
	public static final ConfigEntryString name_invsee = new ConfigEntryString("name_invsee", "invsee");
	public static final ConfigEntryString name_setwarp = new ConfigEntryString("name_setwarp", "setwarp");
	public static final ConfigEntryString name_delwarp = new ConfigEntryString("name_delwarp", "delwarp");
	public static final ConfigEntryString name_unclaim = new ConfigEntryString("name_unclaim", "unclaim");
	public static final ConfigEntryString name_unclaim_all = new ConfigEntryString("name_unclaim_all", "unclaim_all");
	public static final ConfigEntryString name_loaded_chunks = new ConfigEntryString("name_loaded_chunks", "loaded_chunks");
	public static final ConfigEntryString name_world_border = new ConfigEntryString("name_world_border", "worldborder");
	public static final ConfigEntryString name_restart = new ConfigEntryString("name_restart", "restart");
	public static final ConfigEntryString name_set_item_name = new ConfigEntryString("name_set_item_name", "set_item_name");
	public static final ConfigEntryString name_player_lm = new ConfigEntryString("name_player_lm", "player_lm");
}