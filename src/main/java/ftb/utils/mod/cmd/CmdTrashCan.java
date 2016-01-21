package ftb.utils.mod.cmd;

import ftb.lib.api.cmd.*;
import ftb.lib.api.item.BasicInventory;
import ftb.utils.mod.config.FTBUConfigCmd;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;

public class CmdTrashCan extends CommandLM
{
	public CmdTrashCan()
	{ super(FTBUConfigCmd.name_trash_can.get(), CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		ep.displayGUIChest(new BasicInventory(18)
		{
			public String getInventoryName()
			{ return "Trash Can"; }
			
			public boolean hasCustomInventoryName()
			{ return true; }
		});
		
		return null;
	}
}