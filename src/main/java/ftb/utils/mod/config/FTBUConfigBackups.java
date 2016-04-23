package ftb.utils.mod.config;


import ftb.lib.api.config.ConfigEntryBool;
import ftb.lib.api.config.ConfigEntryDouble;
import ftb.lib.api.config.ConfigEntryInt;
import ftb.lib.api.config.ConfigEntryString;
import latmod.lib.annotations.Info;
import latmod.lib.annotations.NumberBounds;

public class FTBUConfigBackups
{
	public static final ConfigEntryBool enabled = new ConfigEntryBool("enabled", true);
	
	@NumberBounds(min = 0, max = 100)
	@Info({"The number of backup files to keep", "More backups = more space used", "0 - Disabled"})
	public static final ConfigEntryInt backups_to_keep = new ConfigEntryInt("backups_to_keep", 12);
	
	@NumberBounds(min = 0.05D, max = 600D)
	@Info({"Timer in hours.", "1.0 - backups every hour", "6.0 - backups every 6 hours", "0.5 - backups every 30 minutes"})
	public static final ConfigEntryDouble backup_timer = new ConfigEntryDouble("backup_timer", 2D);
	
	@NumberBounds(min = 0, max = 9)
	@Info({"0 - Disabled (output = folders)", "1 - Best speed", "9 - Smallest file size"})
	public static final ConfigEntryInt compression_level = new ConfigEntryInt("compression_level", 1);
	
	@Info("Absolute path to backups folder")
	public static final ConfigEntryString folder = new ConfigEntryString("folder", "");
	
	@Info("Prints (current size | total size) when backup is done")
	public static final ConfigEntryBool display_file_size = new ConfigEntryBool("display_file_size", true);
	
	@Info("Run backup in a separated Thread (recommended)")
	public static final ConfigEntryBool use_separate_thread = new ConfigEntryBool("use_separate_thread", true);
	
	@Info("Backups won't run if no players had been online")
	public static final ConfigEntryBool need_online_players = new ConfigEntryBool("need_online_players", true);
	
	public static long backupMillis()
	{ return (long) (backup_timer.getAsDouble() * 3600D * 1000D); }
}