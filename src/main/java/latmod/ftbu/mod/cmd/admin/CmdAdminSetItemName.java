package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LatCore;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdAdminSetItemName extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 1);
		EntityPlayerMP ep = CommandLM.getCommandSenderAsPlayer(ics);
		if(ep.inventory.getCurrentItem() != null)
		{
			ep.inventory.getCurrentItem().setStackDisplayName(LatCore.unsplit(args, " "));
			ep.openContainer.detectAndSendChanges();
			return CommandLM.FINE + "Item name set to '" + ep.inventory.getCurrentItem().getDisplayName() + "'!";
		}
		
		return null;
	}
}