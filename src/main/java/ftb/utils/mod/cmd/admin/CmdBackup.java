package ftb.utils.mod.cmd.admin;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.config.FTBUConfigBackups;
import ftb.utils.world.Backups;
import latmod.lib.LMFileUtils;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CmdBackup extends CommandSubLM
{
	public CmdBackup()
	{
		super("backup", CommandLevel.OP);
		add(new CmdBackupStart("start"));
		add(new CmdBackupStop("stop"));
		add(new CmdBackupGetSize("getsize"));
	}
	
	public static class CmdBackupStart extends CommandLM
	{
		public CmdBackupStart(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			boolean b = Backups.run(ics);
			if(b)
			{
				FTBLib.printChat(BroadcastSender.inst, FTBU.mod.chatComponent("cmd.backup_manual_launch", ics.getCommandSenderName()));
				if(!FTBUConfigBackups.use_separate_thread.get()) Backups.postBackup();
			}
			return b ? null : error(FTBU.mod.chatComponent("cmd.backup_already_running"));
		}
	}
	
	public static class CmdBackupStop extends CommandLM
	{
		public CmdBackupStop(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			if(Backups.thread != null)
			{
				Backups.thread.interrupt();
				Backups.thread = null;
				return FTBU.mod.chatComponent("cmd.backup_stop");
			}
			
			return error(new ChatComponentText("Backup process is not running!"));
		}
	}
	
	public static class CmdBackupGetSize extends CommandLM
	{
		public CmdBackupGetSize(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			String sizeW = LMFileUtils.getSizeS(ics.getEntityWorld().getSaveHandler().getWorldDirectory());
			String sizeT = LMFileUtils.getSizeS(Backups.backupsFolder);
			return new ChatComponentText("Current world size: " + sizeW + ", total backups folder size: " + sizeT);
		}
	}
}