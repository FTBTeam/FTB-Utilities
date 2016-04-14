package ftb.utils.mod.cmd.admin;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.mod.config.FTBUConfigBackups;
import ftb.utils.world.Backups;
import latmod.lib.LMFileUtils;
import net.minecraft.command.*;
import net.minecraft.util.IChatComponent;

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
				FTBLib.printChat(BroadcastSender.inst, FTBULang.backup_manual_launch.chatComponent(ics.getCommandSenderName()));
				if(!FTBUConfigBackups.use_separate_thread.getAsBoolean()) Backups.postBackup();
			}
			return b ? null : error(FTBULang.backup_already_running.chatComponent());
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
				return FTBULang.backup_stop.chatComponent();
			}
			
			return error(FTBULang.backup_not_running.chatComponent());
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
			return FTBULang.backup_size.chatComponent(sizeW, sizeT);
		}
	}
}