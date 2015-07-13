package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LMFileUtils;
import latmod.ftbu.mod.backups.Backups;
import net.minecraft.command.ICommandSender;

public class CmdAdminBackup extends SubCommand
{
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[] { "now", "stop", "deleteall", "getsize" };
		return null;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args.length > 0)
		{
			if(args[0].equals("deleteall"))
			{
				if(Backups.thread != null) return "Backup in progress!";
				Backups.thread = new Thread("LM_Backups_delete")
				{
					public void run()
					{
						LMFileUtils.delete(Backups.backupsFolder);
						Backups.backupsFolder.mkdirs();
						LatCoreMC.printChat(ics, "Done!");
						Backups.thread = null;
					}
				};
				
				Backups.thread.start();
				return CommandLM.FINE + "Deleting all backups...";
			}
			else if(args[0].equals("stop"))
			{
				if(Backups.thread != null)
				{
					Backups.thread.interrupt();
					Backups.thread = null;
					return CommandLM.FINE + "Backup process stopped!";
				}
				
				return "Backup process is not running!";
			}
			else if(args[0].equals("getsize"))
			{
				String sizeW = LMFileUtils.getSizeS(ics.getEntityWorld().getSaveHandler().getWorldDirectory());
				String sizeT = LMFileUtils.getSizeS(Backups.backupsFolder);
				
				return CommandLM.FINE + "Current world size: " + sizeW + ", total backups size: " + sizeT;
			}
		}
		
		Backups.commandOverride = true;
		Backups.shouldRun = true;
		boolean b = Backups.run();
		Backups.commandOverride = false;
		if(b) LatCoreMC.printChat(BroadcastSender.inst, ics.getCommandSenderName() + " launched manual backup!");
		return b ? null : "Backup in progress!";
	}
}