package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyByte;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyShort;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigBackups
{
    public static final PropertyBool ENABLED = new PropertyBool(true);
    public static final PropertyBool SILENT = new PropertyBool(false);
    public static final PropertyShort BACKUPS_TO_KEEP = new PropertyShort(12, 0, 32000);
    public static final PropertyDouble BACKUP_TIMER = new PropertyDouble(2D).setMin(0.05D).setMax(600D);
    public static final PropertyByte COMPRESSION_LEVEL = new PropertyByte(1, 0, 9);
    public static final PropertyString FOLDER = new PropertyString("");
    public static final PropertyBool DISPLAY_FILE_SIZE = new PropertyBool(true);
    public static final PropertyBool USE_SEPARATE_THREAD = new PropertyBool(true);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfig(FTBUFinals.MOD_ID, "backups.enabled", ENABLED);
        reg.addConfig(FTBUFinals.MOD_ID, "backups.silent", SILENT);
        reg.addConfig(FTBUFinals.MOD_ID, "backups.backups_to_keep", BACKUPS_TO_KEEP).setInfo("The number of backup files to keep", "ore backups = more space used", "0 - Infinite");
        reg.addConfig(FTBUFinals.MOD_ID, "backups.backup_timer", BACKUP_TIMER).setInfo("Timer in hours", "1.0 - backups every hour", "6.0 - backups every 6 hours", "0.5 - backups every 30 minutes");
        reg.addConfig(FTBUFinals.MOD_ID, "backups.compression_level", COMPRESSION_LEVEL).setInfo("0 - Disabled (output = folders)", "1 - Best speed", "9 - Smallest file size");
        reg.addConfig(FTBUFinals.MOD_ID, "backups.folder", FOLDER).setInfo("Absolute path to backups folder");
        reg.addConfig(FTBUFinals.MOD_ID, "backups.display_file_size", DISPLAY_FILE_SIZE).setInfo("Prints (current size | total size) when backup is done");
        reg.addConfig(FTBUFinals.MOD_ID, "backups.use_separate_thread", USE_SEPARATE_THREAD).setInfo("Run backup in a separated Thread (recommended)");
    }

    public static long backupMillis()
    {
        return (long) (BACKUP_TIMER.getDouble() * 3600D * 1000D);
    }
}