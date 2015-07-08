package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.SubCommand;
import latmod.ftbu.mod.backups.Backups;
import net.minecraft.command.ICommandSender;

public class CmdAdminBackup extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		LatCoreMC.printChatAll(ics.getCommandSenderName() + " launched manual backup!");
		Backups.shouldRun = true;
		if(!Backups.run()) return "Backup in progress!";
		return null;
	}
}