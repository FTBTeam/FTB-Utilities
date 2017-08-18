package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.events.registry.RegisterConfigEvent;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyByte;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyShort;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigBackups
{
	public static final PropertyBool ENABLED = new PropertyBool(true);
	public static final PropertyBool SILENT = new PropertyBool(false);
	public static final PropertyShort BACKUPS_TO_KEEP = new PropertyShort(12, 0, 32000).setUnsigned();
	public static final PropertyDouble BACKUP_TIMER = new PropertyDouble(2D, 0.05D, 600D);
	public static final PropertyByte COMPRESSION_LEVEL = new PropertyByte(1, 0, 9).setUnsigned();
	public static final PropertyString FOLDER = new PropertyString("");
	public static final PropertyBool DISPLAY_FILE_SIZE = new PropertyBool(true);
	public static final PropertyBool USE_SEPARATE_THREAD = new PropertyBool(true);

	public static void init(RegisterConfigEvent event)
	{
		String id = FTBUFinals.MOD_ID + ".backups";
		event.register(id, "enabled", ENABLED).setNameLangKey(GuiLang.LABEL_ENABLED.getName());
		event.register(id, "silent", SILENT);
		event.register(id, "backups_to_keep", BACKUPS_TO_KEEP);
		event.register(id, "backup_timer", BACKUP_TIMER);
		event.register(id, "compression_level", COMPRESSION_LEVEL);
		event.register(id, "folder", FOLDER);
		event.register(id, "display_file_size", DISPLAY_FILE_SIZE);
		event.register(id, "use_separate_thread", USE_SEPARATE_THREAD);
	}

	public static long backupMillis()
	{
		return (long) (BACKUP_TIMER.getDouble() * 3600D * 1000D);
	}
}