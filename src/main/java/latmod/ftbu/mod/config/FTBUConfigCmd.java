package latmod.ftbu.mod.config;

import latmod.ftbu.api.guide.GuideInfo;
import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class FTBUConfigCmd
{
	public static final ConfigGroup group = new ConfigGroup("commands");
	
	@GuideInfo(info = "Command name for admin command.", def = "admin")
	public static final ConfigEntryString commandNameAdmin = new ConfigEntryString("name_cmd_admin", "admin");
	
	@GuideInfo(info = "Max home count for players", def = "0")
	public static final ConfigEntryInt maxHomesPlayer = new ConfigEntryInt("maxHomesPlayer", new IntBounds(0, 0, 32000));
	
	@GuideInfo(info = "Max home count for admins", def = "100")
	public static final ConfigEntryInt maxHomesAdmin = new ConfigEntryInt("maxHomesAdmin", new IntBounds(100, 0, 32000));
	
	@GuideInfo(info = "Can use /home to teleport to another dimension", def = "true")
	public static final ConfigEntryBool crossDimHomes = new ConfigEntryBool("crossDimHomes", true);
	
	public static final ConfigEntryBool back = new ConfigEntryBool("back", true);
	public static final ConfigEntryBool spawn = new ConfigEntryBool("spawn", true);
	public static final ConfigEntryBool tplast = new ConfigEntryBool("tplast", true);
	public static final ConfigEntryBool warp = new ConfigEntryBool("warp", true);
	public static final ConfigEntryBool home = new ConfigEntryBool("home", true);
	
	public static final ConfigEntryBool admin_backup = new ConfigEntryBool("admin_backup", true);
	public static final ConfigEntryBool admin_edit_config = new ConfigEntryBool("admin_edit_config", true);
	public static final ConfigEntryBool admin_invsee = new ConfigEntryBool("admin_invsee", true);
	public static final ConfigEntryBool admin_warps = new ConfigEntryBool("admin_warps", true);
	public static final ConfigEntryBool admin_edit_chunks = new ConfigEntryBool("admin_edit_chunks", true);
	public static final ConfigEntryBool admin_world_border = new ConfigEntryBool("admin_world_border", true);
	
	public static void load(ConfigFile f)
	{
		group.addAll(FTBUConfigCmd.class);
		f.add(group);
	}
}