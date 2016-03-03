package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBU;
import ftb.utils.world.FTBUWorldDataMP;
import net.minecraft.command.*;
import net.minecraft.util.IChatComponent;

public class CmdDelWarp extends CommandLM
{
	public CmdDelWarp()
	{ super("delwarp", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i) throws CommandException
	{
		if(i == 0) return FTBUWorldDataMP.inst.warps.list();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		if(FTBUWorldDataMP.inst.warps.set(args[0], null)) return FTBU.mod.chatComponent("cmd.warp_del", args[0]);
		return error(FTBU.mod.chatComponent("cmd.warp_not_set", args[0]));
	}
}