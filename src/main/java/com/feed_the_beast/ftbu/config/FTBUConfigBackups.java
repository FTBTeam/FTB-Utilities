package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
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

	public static void init(IFTBLibRegistry reg)
	{
		String id = FTBUFinals.MOD_ID + ".backups";
		reg.addConfig(id, "enabled", ENABLED).setNameLangKey(GuiLang.LABEL_ENABLED.getName());
		reg.addConfig(id, "silent", SILENT);
		reg.addConfig(id, "backups_to_keep", BACKUPS_TO_KEEP);
		reg.addConfig(id, "backup_timer", BACKUP_TIMER);
		reg.addConfig(id, "compression_level", COMPRESSION_LEVEL);
		reg.addConfig(id, "folder", FOLDER);
		reg.addConfig(id, "display_file_size", DISPLAY_FILE_SIZE);
		reg.addConfig(id, "use_separate_thread", USE_SEPARATE_THREAD);
	}

	public static long backupMillis()
	{
		return (long) (BACKUP_TIMER.getDouble() * 3600D * 1000D);
	}
}