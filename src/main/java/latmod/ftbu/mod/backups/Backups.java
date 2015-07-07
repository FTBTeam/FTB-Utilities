package latmod.ftbu.mod.backups;

import java.io.File;
import java.util.*;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.*;
import net.minecraft.world.World;

public class Backups
{
	public static boolean hasAromaBackup;
	public static File backupsFolder;
	public static File backupsFile;
	public static final FastMap<Long, String> backupMap = new FastMap<Long, String>();
	public static long lastTimeRun = -1;
	public static boolean shouldRun = false;
	public static ThreadBackup thread = null;
	
	public static void init()
	{
		hasAromaBackup = LatCoreMC.isModInstalled("AromaBackup");
		backupsFolder = new File(FTBUConfig.Backups.inst.folder);
		if(!backupsFolder.exists()) backupsFolder.mkdirs();
		backupsFile = LatCore.newFile(new File(backupsFolder, "backups.txt"));
		
		try
		{
			backupMap.clear();
			FastList<String> l = LatCore.loadFile(backupsFile);
			
			for(String s : l)
			{
				String[] s1 = s.split(": ");
				backupMap.put(Long.parseLong(s1[0]), s1[1]);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			backupMap.clear();
		}
		
		LatCoreMC.logger.info("Backups folder created @ " + backupsFolder.getAbsolutePath());
	}
	
	public static void saveBackupsMapFile()
	{
		try
		{
			FastList<String> l = new FastList<String>();
			
			for(int i = 0; i < backupMap.size(); i++)
				l.add(backupMap.keys.get(i) + ": " + backupMap.values.get(i));
			
			l.sort(null);
			LatCore.saveFile(backupsFile, l);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public static void run(World w, boolean inThread)
	{
		if(FTBUConfig.Backups.inst.backupsToKeep <= 0) return;
		
		if(thread != null) return;
		if(!shouldRun) return;
		shouldRun = false;
		
		thread = new ThreadBackup(w);
		//if(!inThread) thread.run(); else
		thread.start();
	}
	
	public static long getSecondsUntilNextBackup()
	{
		return ((lastTimeRun + FTBUConfig.Backups.inst.backupTimerL) - FTBUTickHandler.currentMillis()) / 1000L;
	}

	public static long addBackup(String s, long l)
	{
		Backups.backupMap.put(Backups.lastTimeRun, s);
		saveBackupsMapFile();
		
		if(Backups.backupMap.size() <= FTBUConfig.Backups.inst.backupsToKeep)
			return 0L;
		
		Long[] keys = Backups.backupMap.keys.toArray(new Long[0]);
		Arrays.sort(keys);
		return keys[0];
	}
}