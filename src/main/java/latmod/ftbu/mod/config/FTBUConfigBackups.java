package latmod.ftbu.mod.config;

import latmod.lib.config.*;
import latmod.lib.util.*;

public class FTBUConfigBackups
{
	public static final ConfigGroup group = new ConfigGroup("backups");
	public static final ConfigEntryBool enabled = new ConfigEntryBool("enabled", true).setInfo("true enables backups");
	public static final ConfigEntryInt backupsToKeep = new ConfigEntryInt("backupsToKeep", new IntBounds(12, 0, 100)).setInfo("The number of backup files to keep. 0 - Disabled. More backups = more space used");
	private static final ConfigEntryFloat backupTimer = new ConfigEntryFloat("backupTimer", new FloatBounds(2F, 0.2F, 600F)).setInfo("Timer in hours.\n1.0 - backups every hour\n6.0 - backups every 6 hours\n0.5 - backups every 30 minutes");
	public static final ConfigEntryInt compressionLevel = new ConfigEntryInt("compressionLevel", new IntBounds(1, 0, 9)).setInfo("0 - Disabled (output = folders)\n1 - Best speed\n9 - Smallest file size");
	public static final ConfigEntryString folder = new ConfigEntryString("folder", "./backups/").setInfo("Absoute path to backups folder");
	public static final ConfigEntryBool displayFileSize = new ConfigEntryBool("displayFileSize", true).setInfo("Prints (current size | total size) when backup is done");
	public static final ConfigEntryBool autoExportInvOnLogout = new ConfigEntryBool("autoExportInvOnLogout", false).setInfo("Same as running '/admin player @p saveinv' on logout");
	//public static final ConfigEntryBool backupOnShutdown = new ConfigEntryBool("backupOnShutdown", false).setInfo("Launches backup when server stops");
	
	public static long backupTimerL()
	{ return (long)(backupTimer.get() * 3600D * 1000D); }
}