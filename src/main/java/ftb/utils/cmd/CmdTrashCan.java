package ftb.utils.cmd;

import ftb.lib.api.cmd.*;
import ftb.lib.api.item.BasicInventory;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CmdTrashCan extends CommandLM
{
	public CmdTrashCan()
	{ super("trash_can", CommandLevel.ALL); }
	
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		ep.displayGUIChest(new BasicInventory(18)
		{
			public String getName()
			{ return "Trash Can"; }
			
			public boolean hasCustomName()
			{ return true; }
		});
	}
}