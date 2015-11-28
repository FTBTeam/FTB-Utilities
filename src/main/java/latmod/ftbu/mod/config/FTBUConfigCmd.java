package latmod.ftbu.mod.config;

import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class FTBUConfigCmd
{
	public static final ConfigGroup group = new ConfigGroup("commands");
	public static final ConfigEntryString commandNameAdmin = new ConfigEntryString("name_cmd_admin", "admin").setInfo("Command name for admin command.");
	public static final ConfigEntryInt maxHomesPlayer = new ConfigEntryInt("maxHomesPlayer", new IntBounds(0, 0, 32000)).setInfo("Max home count for players");
	public static final ConfigEntryInt maxHomesAdmin = new ConfigEntryInt("maxHomesAdmin", new IntBounds(100, 0, 32000)).setInfo("Max home count for admins");
	public static final ConfigEntryBool crossDimHomes = new ConfigEntryBool("crossDimHomes", true).setInfo("Can use /home to teleport to another dimension");
	
	public static final ConfigEntryBool back = new ConfigEntryBool("back", true);
	public static final ConfigEntryBool spawn = new ConfigEntryBool("spawn", true);
	public static final ConfigEntryBool tplast = new ConfigEntryBool("tplast", true);
	public static final ConfigEntryBool warp = new ConfigEntryBool("warp", true);
	public static final ConfigEntryBool home = new ConfigEntryBool("home", true);
	
	public static final ConfigEntryBool admin_backup = new ConfigEntryBool("admin_backup", true);
	public static final ConfigEntryBool admin_invsee = new ConfigEntryBool("admin_invsee", true);
	public static final ConfigEntryBool admin_warps = new ConfigEntryBool("admin_warps", true);
	public static final ConfigEntryBool admin_edit_chunks = new ConfigEntryBool("admin_edit_chunks", true);
	public static final ConfigEntryBool admin_world_border = new ConfigEntryBool("admin_world_border", true);
}