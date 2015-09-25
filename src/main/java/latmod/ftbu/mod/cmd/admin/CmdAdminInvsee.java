package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.cmd.InvSeeInventory;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;

public class CmdAdminInvsee extends CommandLM
{
	public CmdAdminInvsee(String s)
	{ super(s, CommandLevel.OP); }

	public NameType getUsername(String[] args, int i)
	{
		if(i == 0) return NameType.ON;
		return NameType.NONE;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep0 = getCommandSenderAsPlayer(ics);
		EntityPlayerMP ep = getPlayer(ics, args[0]);
		ep0.displayGUIChest(new InvSeeInventory(ep));
		return null;
	}
}