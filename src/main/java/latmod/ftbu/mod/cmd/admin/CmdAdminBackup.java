package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LMFileUtils;
import latmod.ftbu.mod.backups.Backups;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdAdminBackup extends CommandLM
{
	public CmdAdminBackup(String s)
	{ super(s, CommandLevel.OP); }

	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[] { "now", "stop", "deleteall", "getsize" };
		return null;
	}
	
	public IChatComponent onCommand(final ICommandSender ics, String[] args)
	{
		if(args.length > 0)
		{
			if(args[0].equals("deleteall"))
			{
				if(Backups.thread != null) return error(new ChatComponentText("Backup in progress!")); //LANG
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
		}
		
		Backups.commandOverride = true;
		Backups.shouldRun = true;
		boolean b = Backups.run();
		Backups.commandOverride = false;
		if(b) LatCoreMC.printChat(BroadcastSender.inst, ics.getCommandSenderName() + " launched manual backup!");
		return b ? null : error(new ChatComponentText("Backup in progress!"));
	}
}