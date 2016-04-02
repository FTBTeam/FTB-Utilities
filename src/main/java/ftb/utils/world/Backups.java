package ftb.utils.world;

import ftb.lib.*;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.config.FTBUConfigBackups;
import latmod.lib.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
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
		backupsFolder = FTBUConfigBackups.folder.getAsString().isEmpty() ? new File(FTBLib.folderMinecraft, "/backups/") : new File(FTBUConfigBackups.folder.getAsString());
		if(!backupsFolder.exists()) backupsFolder.mkdirs();
		thread = null;
		clearOldBackups();
		logger.info("Backups folder - " + backupsFolder.getAbsolutePath());
	}
	
	public static boolean run(ICommandSender ics)
	{
		if(thread != null) return false;
		boolean auto = !(ics instanceof EntityPlayerMP);
		
		if(auto && !FTBUConfigBackups.enabled.getAsBoolean()) return false;
		
		World w = FTBLib.getServerWorld();
		if(w == null) return false;
		
		IChatComponent c = FTBU.mod.chatComponent("cmd.backup_start", ics.getCommandSenderName());
		c.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
		BroadcastSender.inst.addChatMessage(c);
		
		nextBackup = LMUtils.millis() + FTBUConfigBackups.backupMillis();
		
		if(auto && FTBUConfigBackups.need_online_players.getAsBoolean())
		{
			if(!FTBLib.hasOnlinePlayers() && !hadPlayer) return true;
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
		
		File wd = w.getSaveHandler().getWorldDirectory();
		
		if(FTBUConfigBackups.use_separate_thread.getAsBoolean())
		{
			thread = new ThreadBackup(wd);
			thread.start();
		}
		else
		{
			ThreadBackup.doBackup(wd);
		}
		
		return true;
	}
	
	public static void clearOldBackups()
	{
		String[] s = backupsFolder.list();
		
		if(s.length > FTBUConfigBackups.backups_to_keep.getAsInt())
		{
			Arrays.sort(s);
			
			int j = s.length - FTBUConfigBackups.backups_to_keep.getAsInt();
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