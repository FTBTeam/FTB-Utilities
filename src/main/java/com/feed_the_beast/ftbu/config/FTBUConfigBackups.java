package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.impl.PropertyBool;
import com.feed_the_beast.ftbl.api.config.impl.PropertyDouble;
import com.feed_the_beast.ftbl.api.config.impl.PropertyInt;
import com.feed_the_beast.ftbl.api.config.impl.PropertyString;
import com.latmod.lib.annotations.Info;
import net.minecraftforge.common.util.Constants;

public class FTBUConfigBackups
{
    public static final PropertyBool ENABLED = new PropertyBool(true);

    @Info({"The number of backup files to keep", "More backups = more space used", "0 - Disabled"})
    public static final PropertyInt BACKUPS_TO_KEEP = new PropertyInt(Constants.NBT.TAG_BYTE, 12).setMin(0).setMax(100);

    @Info({"Timer in hours", "1.0 - backups every hour", "6.0 - backups every 6 hours", "0.5 - backups every 30 minutes"})
    public static final PropertyDouble BACKUP_TIMER = new PropertyDouble(2D).setMin(0.05D).setMax(600D);

    @Info({"0 - Disabled (output = folders)", "1 - Best speed", "9 - Smallest file size"})
    public static final PropertyInt COMPRESSION_LEVEL = new PropertyInt(Constants.NBT.TAG_BYTE, 1).setMin(0).setMax(9);

    @Info("Absolute path to backups folder")
    public static final PropertyString FOLDER = new PropertyString("");

    @Info("Prints (current size | total size) when backup is done")
    public static final PropertyBool DISPLAY_FILE_SIZE = new PropertyBool(true);

    @Info("Run backup in a separated Thread (recommended)")
    public static final PropertyBool USE_SEPARATE_THREAD = new PropertyBool(true);

    public static long backupMillis()
    {
        return (long) (BACKUP_TIMER.getInt() * 3600D * 1000D);
    }
}