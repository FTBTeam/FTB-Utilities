package latmod.ftbu.mod.cmd.admin;

import ftb.lib.*;
import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.util.CommandFTBU;
import latmod.ftbu.world.Backups;
import latmod.lib.LMFileUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdAdminBackup extends CommandFTBU
{
	public CmdAdminBackup(String s)
	{ super(s, CommandLevel.OP); }

	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[] { "start", "stop", "deleteall", "getsize" };
		return null;
	}
	
	public IChatComponent onCommand(final ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		
		if(args[0].equals("start"))
		{
			Backups.commandOverride = true;
			Backups.shouldRun = true;
			boolean b = Backups.run();
			Backups.commandOverride = false;
			if(b) FTBLib.printChat(BroadcastSender.inst, ics.getCommandSenderName() + " launched manual backup!");
			return b ? null : error(new ChatComponentText("Backup in progress!"));
		}
		if(args[0].equals("deleteall"))
		{
			if(Backups.thread != null) return error(new ChatComponentText("Backup in progress!"));
			Backups.thread = new Thread("LM_Backups_delete")
			{
				public void run()
				{
					LMFileUtils.delete(Backups.backupsFolder);
					Backups.backupsFolder.mkdirs();
					FTBLib.printChat(ics, "Done!");
					Backups.thread = null;
				}
			};
			
			Backups.thread.start();
			return new ChatComponentText("Deleting all backups...");
		}
		else if(args[0].equals("stop"))
		{
			if(Backups.thread != null)
			{
				Backups.thread.interrupt();
				Backups.thread = null;
				return new ChatComponentText("Backup process stopped!");
			}
			
			return error(new ChatComponentText("Backup process is not running!"));
		}
		else if(args[0].equals("getsize"))
		{
			String sizeW = LMFileUtils.getSizeS(ics.getEntityWorld().getSaveHandler().getWorldDirectory());
			String sizeT = LMFileUtils.getSizeS(Backups.backupsFolder);
			
			return new ChatComponentText("Current world size: " + sizeW + ", total backups folder size: " + sizeT);
		}
		
		return null;
	}
}