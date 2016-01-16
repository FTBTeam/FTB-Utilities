package latmod.ftbu.mod.cmd.admin;

import ftb.lib.*;
import ftb.lib.cmd.*;
import latmod.ftbu.world.Backups;
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
		add(new CmdBackupDeleteAll("delete_all"));
		add(new CmdBackupGetSize("getsize"));
	}
	
	public static class CmdBackupStart extends CommandLM
	{
		public CmdBackupStart(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			Backups.commandOverride = true;
			Backups.shouldRun = true;
			boolean b = Backups.run();
			Backups.commandOverride = false;
			if(b) FTBLib.printChat(BroadcastSender.inst, ics.getName() + " launched manual backup!");
			return b ? null : error(new ChatComponentText("Backup in progress!"));
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
				return new ChatComponentText("Backup process stopped!");
			}
			
			return error(new ChatComponentText("Backup process is not running!"));
		}
	}
	
	public static class CmdBackupDeleteAll extends CommandLM
	{
		public CmdBackupDeleteAll(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(final ICommandSender ics, String[] args) throws CommandException
		{
			if(Backups.thread != null) return error(new ChatComponentText("Backup process already running!"));
			Backups.thread = new Thread("LM_Backups_delete")
			{
				public void run()
				{
					LMFileUtils.delete(Backups.backupsFolder);
					Backups.backupsFolder.mkdirs();
					FTBLib.printChat(ics, "Done!");
					Backups.shouldKillThread = true;
				}
			};
			
			Backups.thread.start();
			return new ChatComponentText("Deleting all backups...");
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