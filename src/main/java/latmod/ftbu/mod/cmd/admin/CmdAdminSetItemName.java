package latmod.ftbu.mod.cmd.admin;

import latmod.core.util.LMStringUtils;
import latmod.ftbu.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdAdminSetItemName extends CommandLM
{
	public CmdAdminSetItemName(String s)
	{ super(s, CommandLevel.OP); }

	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		if(ep.inventory.getCurrentItem() != null)
		{
			ep.inventory.getCurrentItem().setStackDisplayName(LMStringUtils.unsplit(args, " "));
			ep.openContainer.detectAndSendChanges();
			return new ChatComponentText("Item name set to '" + ep.inventory.getCurrentItem().getDisplayName() + "'!");
		}
		
		return null;
	}
}