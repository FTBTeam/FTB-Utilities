package latmod.ftbu.mod.config;

import java.io.File;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.util.LatCore;

import com.google.gson.annotations.Expose;

public class ConfigBackups
{
	private static File saveFile;
	
	@Expose public Boolean enabled;
	@Expose public Integer backupsToKeep;
	@Expose private Float backupTimer;
	//@Expose public Boolean backupOnShutdown;
	@Expose public Boolean compress;
	@Expose public String folder;
	@Expose public Boolean displayFileSize;
	public long backupTimerL;
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/backups.txt");
		FTBUConfig.backups = LatCore.fromJsonFile(saveFile, ConfigBackups.class);
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
		if(compress == null) compress = true;
		if(folder == null) folder = "./latmod/backups/";
		if(displayFileSize == null) displayFileSize = true;
		
		backupTimerL = (long)(backupTimer.doubleValue() * 3600D * 1000D);
	}
	
	public static void save()
	{
		if(FTBUConfig.backups == null) load();
		if(!LatCore.toJsonFile(saveFile, FTBUConfig.backups))
			LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
	}
	
	public static void saveReadme(FTBUReadmeEvent e)
	{
		FTBUReadmeEvent.ReadmeFile.Category backups = e.file.get("latmod/ftbu/backups.txt");
		backups.add("enabled", "true enables backups", false);
		backups.add("backupsToKeep", "The number of backup files to keep. 0 - Disabled. More backups = more space used.", 12);
		backups.add("backupTimer", "Timer in hours. Can be .x, 1.0 - backups every hour, 6.0 - backups every 6 hours, 0.5 - backups every 30 minutes.", 2F);
		//backups.add("backupOnShutdown", "Launches backup when server stops.", false);
		backups.add("compress", "true to compress into .zip, false to backup as folders.", true);
		backups.add("folder", "Absoute path to backups folder, blank means /latmod/backups/.", "Blank");
		backups.add("displayFileSize", "Prints (current size | total size) when backup is done", true);
	}
}