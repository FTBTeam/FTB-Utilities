package ftb.utils.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.cmd.InvSeeInventory;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CmdInvsee extends CommandLM
{
	public CmdInvsee()
	{ super("invsee", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player>"; }
	
	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		EntityPlayerMP ep0 = getCommandSenderAsPlayer(ics);
		EntityPlayerMP ep = getPlayer(server, ics, args[0]);
		ep0.displayGUIChest(new InvSeeInventory(ep));
	}
}