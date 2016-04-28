package ftb.utils.cmd;

import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.lib.api.item.BasicInventory;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CmdTrashCan extends CommandLM
{
	public CmdTrashCan()
	{ super("trash_can", CommandLevel.ALL); }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		ep.displayGUIChest(new BasicInventory(18)
		{
			@Override
			public String getName()
			{ return "Trash Can"; }
			
			@Override
			public boolean hasCustomName()
			{ return true; }
		});
	}
}