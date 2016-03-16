package ftb.utils.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.cmd.InvSeeInventory;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdInvsee extends CommandLM
{
	public CmdInvsee()
	{ super("invsee", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player>"; }
	
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		EntityPlayerMP ep0 = getCommandSenderAsPlayer(ics);
		EntityPlayerMP ep = getPlayer(ics, args[0]);
		ep0.displayGUIChest(new InvSeeInventory(ep));
	}
}