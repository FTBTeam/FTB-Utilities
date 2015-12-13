package latmod.ftbu.mod.config;

import latmod.lib.config.*;
import latmod.lib.util.*;

public class FTBUConfigBackups
{
	public static final ConfigGroup group = new ConfigGroup("backups");
	public static final ConfigEntryBool enabled = new ConfigEntryBool("enabled", true).setInfo("true enables backups");
	public static final ConfigEntryInt backups_to_keep = new ConfigEntryInt("backups_to_keep", new IntBounds(12, 0, 100)).setInfo("The number of backup files to keep. 0 - Disabled. More backups = more space used");
	public static final ConfigEntryDouble backup_timer = new ConfigEntryDouble("backup_timer", new DoubleBounds(2F, 0.2F, 600F)).setInfo("Timer in hours.\n1.0 - backups every hour\n6.0 - backups every 6 hours\n0.5 - backups every 30 minutes");
	public static final ConfigEntryInt compression_level = new ConfigEntryInt("compression_level", new IntBounds(1, 0, 9)).setInfo("0 - Disabled (output = folders)\n1 - Best speed\n9 - Smallest file size");
	public static final ConfigEntryString folder = new ConfigEntryString("folder", "./backups/").setInfo("Absoute path to backups folder");
	public static final ConfigEntryBool display_file_size = new ConfigEntryBool("display_file_size", true).setInfo("Prints (current size | total size) when backup is done");
}