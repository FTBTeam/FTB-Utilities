package latmod.ftbu.backups;

import java.io.File;
import java.util.Arrays;

import latmod.ftbu.mod.FTBUTicks;
import latmod.ftbu.mod.config.FTBUConfigBackups;
import latmod.ftbu.util.LatCoreMC;
import latmod.lib.LMFileUtils;
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
		backupsFolder = FTBUConfigBackups.folder.get().isEmpty() ? new File(LatCoreMC.localFolder, "backups/") : new File(FTBUConfigBackups.folder.get());
		if(!backupsFolder.exists()) backupsFolder.mkdirs();
		thread = null;
		clearOldBackups();
		LatCoreMC.logger.info("Backups folder created @ " + backupsFolder.getAbsolutePath());
	}
	
	public static boolean enabled()
	{ return commandOverride || FTBUConfigBackups.enabled.get(); }
	
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
	{ return ((lastTimeRun + FTBUConfigBackups.backupTimerL()) - FTBUTicks.currentMillis()) / 1000L; }
	
	public static void clearOldBackups()
	{
		String[] s = backupsFolder.list();
		
		if(s.length > FTBUConfigBackups.backupsToKeep.get())
		{
			Arrays.sort(s);
			
			int j = s.length - FTBUConfigBackups.backupsToKeep.get();
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