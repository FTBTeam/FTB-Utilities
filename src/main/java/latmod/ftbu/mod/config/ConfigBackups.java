package latmod.ftbu.mod.config;

import java.io.File;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.api.readme.ReadmeInfo;
import latmod.ftbu.core.util.*;

public class ConfigBackups
{
	private static transient File saveFile;
	
	@ReadmeInfo(info = "true enables backups", def = "false")
	public Boolean enabled;
	
	@ReadmeInfo(info = "The number of backup files to keep. 0 - Disabled. More backups = more space used.", def = "12")
	public Integer backupsToKeep;
	
	@ReadmeInfo(info = "Timer in hours. 1.0 - backups every hour, 6.0 - backups every 6 hours, 0.5 - backups every 30 minutes.", def = "2.0")
	private Float backupTimer;
	
	//@ReadmeInfo(info = "Launches backup when server stops.", def = "false")
	//public Boolean backupOnShutdown;
	
	@ReadmeInfo(info = "0 - Disabled (output = folders), Min - 1 (Best speed), Max - 9 (Smallest file),", def = "1")
	public Integer compressionLevel;
	
	@ReadmeInfo(info = "Absoute path to backups folder, blank means /latmod/backups/.", def = "Blank")
	public String folder;
	
	@ReadmeInfo(info = "Prints (current size | total size) when backup is done", def = "true")
	public Boolean displayFileSize;
	
	@ReadmeInfo(info = "Same as running '/admin player @p saveinv' on logout", def = "false")
	public Boolean autoExportInvOnLogout;
	
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
}