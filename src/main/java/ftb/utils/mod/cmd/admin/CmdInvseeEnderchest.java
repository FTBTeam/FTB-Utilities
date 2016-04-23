package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdInvseeEnderchest extends CommandLM
{
	public CmdInvseeEnderchest()
	{ super("invsee_enderchest", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player>"; }
	
	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		EntityPlayerMP ep0 = getCommandSenderAsPlayer(ics);
		EntityPlayerMP ep = getPlayer(ics, args[0]);
		ep0.displayGUIChest(ep.getInventoryEnderChest());
	}
}