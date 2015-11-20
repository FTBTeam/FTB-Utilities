package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.mod.cmd.InvSeeInventory;
import latmod.ftbu.util.CommandFTBU;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;

public class CmdAdminInvsee extends CommandFTBU
{
	public CmdAdminInvsee(String s)
	{ super(s, CommandLevel.OP); }

	public Boolean getUsername(String[] args, int i)
	{ return (i == 0) ? Boolean.TRUE : null; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep0 = getCommandSenderAsPlayer(ics);
		EntityPlayerMP ep = getPlayer(ics, args[0]);
		ep0.displayGUIChest(new InvSeeInventory(ep));
		return null;
	}
}