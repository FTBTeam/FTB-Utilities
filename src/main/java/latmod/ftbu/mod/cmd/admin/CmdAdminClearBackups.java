package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.SubCommand;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.mod.backups.Backups;
import net.minecraft.command.ICommandSender;

public class CmdAdminClearBackups extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		LatCoreMC.printChat(ics, "Deleting all backups...");
		LatCore.deleteFile(Backups.backupsFolder);
		Backups.backupsFolder.mkdirs();
		LatCoreMC.printChat(ics, "Done!");
		return null;
	}
}