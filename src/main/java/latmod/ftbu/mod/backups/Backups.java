package latmod.ftbu.mod.backups;

import java.io.File;
import java.util.Arrays;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.mod.FTBUTickHandler;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.world.World;

public class Backups
{
	public static File backupsFolder;
	public static long lastTimeRun = -1;
	public static boolean shouldRun = false;
	public static boolean canRun = false;
	
	public static void init()
	{
		backupsFolder = FTBUConfig.backups.folder.isEmpty() ? new File(LatCoreMC.latmodFolder, "backups/") : new File(FTBUConfig.backups.folder);
		if(!backupsFolder.exists()) backupsFolder.mkdirs();
		canRun = true;
		clearOldBackups();
		LatCoreMC.logger.info("Backups folder created @ " + backupsFolder.getAbsolutePath());
	}
	
	public static boolean run()
	{
		if(!canRun || !shouldRun || !FTBUConfig.backups.enabled) return false;
		if(!FTBUConfig.general.isDedi()) return false;
		World w = LatCoreMC.getServerWorld();
		if(w == null) return false;
		shouldRun = false;
		new ThreadBackup(w).start();
		return true;
	}
	
	public static long getSecondsUntilNextBackup()
	{
		return ((lastTimeRun + FTBUConfig.backups.backupTimerL) - FTBUTickHandler.currentMillis()) / 1000L;
	}
	
	public static void clearOldBackups()
	{
		String[] s = backupsFolder.list();
		
		if(s.length > FTBUConfig.backups.backupsToKeep)
		{
			Arrays.sort(s);
			
			int j = s.length - FTBUConfig.backups.backupsToKeep;
			LatCoreMC.logger.info("Deleting " + j + " old backups");
			
			for(int i = 0; i < j; i++)
			{
				File f = new File(backupsFolder, s[i]);
				if(f.isDirectory())
				{
					LatCoreMC.logger.info("Deleted old backup: " + f.getPath());
					LatCore.deleteFile(f);
				}
			}
		}
	}
}