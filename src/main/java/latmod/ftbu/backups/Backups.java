package latmod.ftbu.backups;

import java.io.File;
import java.util.Arrays;

import latmod.core.util.LMFileUtils;
import latmod.ftbu.mod.FTBUTickHandler;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.util.LatCoreMC;
import net.minecraft.world.World;

public class Backups
{
	public static File backupsFolder;
	public static long lastTimeRun = -1;
	public static boolean shouldRun = false;
	public static Thread thread;
	public static boolean commandOverride = false;
	
	public static void init()
	{
		backupsFolder = FTBUConfig.backups.folder.isEmpty() ? new File(LatCoreMC.latmodFolder, "backups/") : new File(FTBUConfig.backups.folder);
		if(!backupsFolder.exists()) backupsFolder.mkdirs();
		thread = null;
		clearOldBackups();
		LatCoreMC.logger.info("Backups folder created @ " + backupsFolder.getAbsolutePath());
	}
	
	public static boolean enabled()
	{ return commandOverride || (FTBUConfig.backups.enabled && FTBUConfig.general.isDedi()); }
	
	public static boolean run()
	{
		if(thread != null || !shouldRun || !enabled()) return false;
		World w = LatCoreMC.getServerWorld();
		if(w == null) return false;
		shouldRun = false;
		thread = new ThreadBackup(w);
		thread.start();
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
					LMFileUtils.delete(f);
				}
			}
		}
	}
}