package latmod.ftbu.mod.config;

import latmod.lib.config.*;

public class FTBUConfigCmd
{
	public static final ConfigGroup group = new ConfigGroup("commands");
	
	public static final ConfigEntryBool back = new ConfigEntryBool("back", true);
	public static final ConfigEntryBool motd = new ConfigEntryBool("motd", true);
	public static final ConfigEntryBool rules = new ConfigEntryBool("rules", true);
	public static final ConfigEntryBool spawn = new ConfigEntryBool("spawn", true);
	public static final ConfigEntryBool tplast = new ConfigEntryBool("tplast", true);
	public static final ConfigEntryBool warp = new ConfigEntryBool("warp", true);
	
	public static final ConfigEntryBool ftbu_backup_timer = new ConfigEntryBool("ftbu_backup_timer", true);
	public static final ConfigEntryBool ftbu_playerID = new ConfigEntryBool("ftbu_playerID", true);
	public static final ConfigEntryBool ftbu_restart_timer = new ConfigEntryBool("ftbu_restart_timer", true);
	public static final ConfigEntryBool ftbu_tops = new ConfigEntryBool("ftbu_tops", true);
	
	public static final ConfigEntryBool admin_backup = new ConfigEntryBool("admin_backup", true);
	public static final ConfigEntryBool admin_edit_config = new ConfigEntryBool("admin_edit_config", true);
	public static final ConfigEntryBool admin_invsee = new ConfigEntryBool("admin_invsee", true);
	public static final ConfigEntryBool admin_warps = new ConfigEntryBool("admin_warps", true);
	public static final ConfigEntryBool admin_unclaim = new ConfigEntryBool("admin_unclaim", true);
	public static final ConfigEntryBool admin_world_border = new ConfigEntryBool("admin_world_border", true);
	
	public static void load(ConfigFile f)
	{
		group.addAll(FTBUConfigCmd.class);
		f.add(group);
	}
}