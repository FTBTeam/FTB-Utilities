package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.latmod.lib.config.PropertyBool;
import com.latmod.lib.config.PropertyByte;
import com.latmod.lib.config.PropertyDouble;
import com.latmod.lib.config.PropertyString;

public class FTBUConfigBackups
{
    @ConfigValue(id = "backups.enabled", file = FTBUFinals.MOD_ID)
    public static final PropertyBool ENABLED = new PropertyBool(true);

    @ConfigValue(id = "backups.backups_to_keep", file = FTBUFinals.MOD_ID, info = {"The number of backup files to keep", "ore backups = more space used", "0 - Disabled"})
    public static final PropertyByte BACKUPS_TO_KEEP = new PropertyByte(12, 0, 100);

    @ConfigValue(id = "backups.backup_timer", file = FTBUFinals.MOD_ID, info = {"Timer in hours", "1.0 - backups every hour", "6.0 - backups every 6 hours", "0.5 - backups every 30 minutes"})
    public static final PropertyDouble BACKUP_TIMER = new PropertyDouble(2D).setMin(0.05D).setMax(600D);

    @ConfigValue(id = "backups.compression_level", file = FTBUFinals.MOD_ID, info = {"0 - Disabled (output = folders)", "1 - Best speed", "9 - Smallest file size"})
    public static final PropertyByte COMPRESSION_LEVEL = new PropertyByte(1, 0, 9);

    @ConfigValue(id = "backups.folder", file = FTBUFinals.MOD_ID, info = "Absolute path to backups folder")
    public static final PropertyString FOLDER = new PropertyString("");

    @ConfigValue(id = "backups.display_file_size", file = FTBUFinals.MOD_ID, info = "Prints (current size | total size) when backup is done")
    public static final PropertyBool DISPLAY_FILE_SIZE = new PropertyBool(true);

    @ConfigValue(id = "backups.use_separate_thread", file = FTBUFinals.MOD_ID, info = "Run backup in a separated Thread (recommended)")
    public static final PropertyBool USE_SEPARATE_THREAD = new PropertyBool(true);

    public static long backupMillis()
    {
        return (long) (BACKUP_TIMER.getInt() * 3600D * 1000D);
    }
}