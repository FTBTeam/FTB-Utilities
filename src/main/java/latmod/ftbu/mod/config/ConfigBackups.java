package latmod.ftbu.mod.config;

import java.io.File;

import com.google.gson.annotations.Expose;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.api.readme.*;
import latmod.ftbu.core.util.*;

public class ConfigBackups
{
	private static File saveFile;
	
	@Expose public Boolean enabled;
	@Expose public Integer backupsToKeep;
	@Expose private Float backupTimer;
	//@Expose public Boolean backupOnShutdown;
	@Expose public Integer compressionLevel;
	@Expose public String folder;
	@Expose public Boolean displayFileSize;
	@Expose public Boolean autoExportInvOnLogout;
	public long backupTimerL;
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/backups.txt");
		FTBUConfig.backups = LMJsonUtils.fromJsonFile(saveFile, ConfigBackups.class);
		if(FTBUConfig.backups == null) FTBUConfig.backups = new ConfigBackups();
		FTBUConfig.backups.loadDefaults();
		save();
	}
	
	public void loadDefaults()
	{
		if(enabled == null) enabled = false;
		if(backupsToKeep == null) backupsToKeep = 12;
		if(backupTimer == null) backupTimer = 2F;
		//if(backupOnShutdown == null) backupOnShutdown = false;
		if(compressionLevel == null) compressionLevel = 1;
		if(folder == null) folder = "./latmod/backups/";
		if(displayFileSize == null) displayFileSize = true;
		if(autoExportInvOnLogout == null) autoExportInvOnLogout = false;
		
		backupTimerL = (long)(backupTimer.doubleValue() * 3600D * 1000D);
		compressionLevel = MathHelperLM.clampInt(compressionLevel, 0, 9);
	}
	
	public static void save()
	{
		if(FTBUConfig.backups == null) load();
		if(!LMJsonUtils.toJsonFile(saveFile, FTBUConfig.backups))
			LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
	}
	
	public static void saveReadme(ReadmeFile file)
	{
		ReadmeCategory backups = file.get("latmod/ftbu/backups.txt");
		backups.add("enabled", "true enables backups", false);
		backups.add("backupsToKeep", "The number of backup files to keep. 0 - Disabled. More backups = more space used.", 12);
		backups.add("backupTimer", "Timer in hours. 1.0 - backups every hour, 6.0 - backups every 6 hours, 0.5 - backups every 30 minutes.", 2F);
		//backups.add("backupOnShutdown", "Launches backup when server stops.", false);
		backups.add("compressionLevel", "0 - Disabled (output = folders), Min - 1 (Best speed), Max - 9 (Smallest file),", 1);
		backups.add("folder", "Absoute path to backups folder, blank means /latmod/backups/.", "Blank");
		backups.add("displayFileSize", "Prints (current size | total size) when backup is done", true);
		backups.add("autoExportInvOnLogout", "Same as running '/admin player @p saveinv' on logout", false);
	}
}