package ftb.utils.mod.cmd.admin;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.config.FTBUConfigBackups;
import ftb.utils.world.Backups;
import latmod.lib.LMFileUtils;
import net.minecraft.command.*;

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
		
		public void processCommand(ICommandSender ics, String[] args) throws CommandException
		{
			boolean b = Backups.run(ics);
			if(b)
			{
				FTBLib.printChat(BroadcastSender.inst, FTBU.mod.chatComponent("cmd.backup_manual_launch", ics.getName()));
				if(!FTBUConfigBackups.use_separate_thread.get()) Backups.postBackup();
			}
			
			if(b) ics.addChatMessage(FTBU.mod.chatComponent("cmd.backup_already_running"));
		}
	}
	
	public static class CmdBackupStop extends CommandLM
	{
		public CmdBackupStop(String s)
		{ super(s, CommandLevel.OP); }
		
		public void processCommand(ICommandSender ics, String[] args) throws CommandException
		{
			if(Backups.thread != null)
			{
				Backups.thread.interrupt();
				Backups.thread = null;
				ics.addChatMessage(FTBU.mod.chatComponent("cmd.backup_stop"));
				return;
			}
			
			throw new CommandException("ftbu.cmd.backup_not_running");
		}
	}
	
	public static class CmdBackupGetSize extends CommandLM
	{
		public CmdBackupGetSize(String s)
		{ super(s, CommandLevel.OP); }
		
		public void processCommand(ICommandSender ics, String[] args) throws CommandException
		{
			String sizeW = LMFileUtils.getSizeS(ics.getEntityWorld().getSaveHandler().getWorldDirectory());
			String sizeT = LMFileUtils.getSizeS(Backups.backupsFolder);
			ics.addChatMessage(FTBU.mod.chatComponent("cmd.backup_size", sizeW, sizeT));
		}
	}
}