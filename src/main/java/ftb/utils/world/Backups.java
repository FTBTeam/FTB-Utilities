package ftb.utils.world;

import ftb.lib.*;
import ftb.utils.mod.config.FTBUConfigBackups;
import latmod.lib.*;
import net.minecraft.command.server.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.apache.logging.log4j.*;

import java.io.File;
import java.util.Arrays;

public class Backups
{
	public static final Logger logger = LogManager.getLogger("FTBU Backups");
	
	public static File backupsFolder;
	public static long nextBackup = -1L;
	public static ThreadBackup thread = null;
	public static boolean hadPlayer = false;
	
	public static void init()
	{
		backupsFolder = FTBUConfigBackups.folder.get().isEmpty() ? new File(FTBLib.folderMinecraft, "/backups/") : new File(FTBUConfigBackups.folder.get());
		if(!backupsFolder.exists()) backupsFolder.mkdirs();
		thread = null;
		clearOldBackups();
		logger.info("Backups folder created @ " + backupsFolder.getAbsolutePath());
	}
	
	public static boolean run(boolean auto)
	{
		if(thread != null || !FTBUConfigBackups.enabled.get()) return false;
		World w = FTBLib.getServerWorld();
		if(w == null) return false;
		FTBLib.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Starting server backup, expect lag!");
		
		nextBackup = LMUtils.millis() + FTBUConfigBackups.backupMillis();
		
		if(FTBUConfigBackups.need_online_players.get())
		{
			if(!hadPlayer) return true;
			hadPlayer = false;
		}
		
		try
		{
			new CommandSaveOff().processCommand(FTBLib.getServer(), new String[0]);
			new CommandSaveAll().processCommand(FTBLib.getServer(), new String[0]);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		if(FTBUConfigBackups.use_separate_thread.get())
		{
			thread = new ThreadBackup(w);
			thread.start();
		}
		else
		{
			ThreadBackup.doBackup(w.getSaveHandler().getWorldDirectory());
		}
		
		return true;
	}
	
	public static void clearOldBackups()
	{
		String[] s = backupsFolder.list();
		
		if(s.length > FTBUConfigBackups.backups_to_keep.get())
		{
			Arrays.sort(s);
			
			int j = s.length - FTBUConfigBackups.backups_to_keep.get();
			logger.info("Deleting " + j + " old backups");
			
			for(int i = 0; i < j; i++)
			{
				File f = new File(backupsFolder, s[i]);
				if(f.isDirectory())
				{
					logger.info("Deleted old backup: " + f.getPath());
					LMFileUtils.delete(f);
				}
			}
		}
	}
	
	public static void postBackup()
	{
		try
		{
			new CommandSaveOn().processCommand(FTBLib.getServer(), new String[0]);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}