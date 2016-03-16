package ftb.utils.cmd;

import ftb.lib.api.cmd.*;
import ftb.lib.api.item.BasicInventory;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdTrashCan extends CommandLM
{
	public CmdTrashCan()
	{ super("trash_can", CommandLevel.ALL); }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		ep.displayGUIChest(new BasicInventory(18)
		{
			public String getInventoryName()
			{ return "Trash Can"; }
			
			public boolean hasCustomInventoryName()
			{ return true; }
		});
	}
}