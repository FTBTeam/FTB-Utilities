package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.api_impl.config.PropertyBool;
import com.feed_the_beast.ftbl.api_impl.config.PropertyDouble;
import com.feed_the_beast.ftbl.api_impl.config.PropertyInt;
import com.feed_the_beast.ftbl.api_impl.config.PropertyString;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraftforge.common.util.Constants;

public class FTBUConfigBackups
{
    @ConfigValue(id = "backups.enabled", file = FTBUFinals.MOD_ID)
    public static final PropertyBool ENABLED = new PropertyBool(true);

    @ConfigValue(id = "backups.backups_to_keep", file = FTBUFinals.MOD_ID, info = "The number of backup files to keep\nore backups = more space used\n0 - Disabled")
    public static final PropertyInt BACKUPS_TO_KEEP = new PropertyInt(Constants.NBT.TAG_BYTE, 12).setMin(0).setMax(100);

    @ConfigValue(id = "backups.backup_timer", file = FTBUFinals.MOD_ID, info = "Timer in hours\n1.0 - backups every hour\n6.0 - backups every 6 hours\n0.5 - backups every 30 minutes")
    public static final PropertyDouble BACKUP_TIMER = new PropertyDouble(2D).setMin(0.05D).setMax(600D);

    @ConfigValue(id = "backups.compression_level", file = FTBUFinals.MOD_ID, info = "0 - Disabled (output = folders)\n1 - Best speed\n9 - Smallest file size")
    public static final PropertyInt COMPRESSION_LEVEL = new PropertyInt(Constants.NBT.TAG_BYTE, 1).setMin(0).setMax(9);

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