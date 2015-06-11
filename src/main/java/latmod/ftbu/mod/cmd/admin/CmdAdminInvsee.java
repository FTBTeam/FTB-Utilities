package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.cmd.InvSeeInventory;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdAdminInvsee extends SubCommand
{
	public NameType getUsername(String[] args, int i)
	{
		if(i == 0) return NameType.ON;
		return NameType.NONE;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 1);
		EntityPlayerMP ep0 = CommandLM.getCommandSenderAsPlayer(ics);
		EntityPlayerMP ep = CommandLM.getPlayer(ics, args[0]);
		ep0.displayGUIChest(new InvSeeInventory(ep));
		return null;
	}
}