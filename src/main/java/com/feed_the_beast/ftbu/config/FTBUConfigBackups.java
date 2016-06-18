package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryDouble;
import com.feed_the_beast.ftbl.api.config.ConfigEntryInt;
import com.feed_the_beast.ftbl.api.config.ConfigEntryString;
import com.latmod.lib.annotations.Info;
import com.latmod.lib.annotations.NumberBounds;

public class FTBUConfigBackups
{
    @NumberBounds(min = 0, max = 100)
    @Info({"The number of backup files to keep", "More backups = more space used", "0 - Disabled"})
    public static final ConfigEntryInt backups_to_keep = new ConfigEntryInt(12);

    @NumberBounds(min = 0.05D, max = 600D)
    @Info({"Timer in hours", "1.0 - backups every hour", "6.0 - backups every 6 hours", "0.5 - backups every 30 minutes"})
    public static final ConfigEntryDouble backup_timer = new ConfigEntryDouble(2D);

    @NumberBounds(min = 0, max = 9)
    @Info({"0 - Disabled (output = folders)", "1 - Best speed", "9 - Smallest file size"})
    public static final ConfigEntryInt compression_level = new ConfigEntryInt(1);

    @Info("Absolute path to backups folder")
    public static final ConfigEntryString folder = new ConfigEntryString("");

    @Info("Prints (current size | total size) when backup is done")
    public static final ConfigEntryBool display_file_size = new ConfigEntryBool(true);

    @Info("Run backup in a separated Thread (recommended)")
    public static final ConfigEntryBool use_separate_thread = new ConfigEntryBool(true);

    @Info("Backups won't run if no players had been online")
    public static final ConfigEntryBool need_online_players = new ConfigEntryBool(true);

    public static long backupMillis()
    {
        return (long) (backup_timer.getAsInt() * 3600D * 1000D);
    }
}