package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMWorldServer;
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
		if(i == 0) return LMWorldServer.inst.warps.list();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		if(LMWorldServer.inst.warps.set(args[0], null)) return FTBULang.warp_del.chatComponent(args[0]);
		return error(FTBULang.warp_not_set.chatComponent(args[0]));
	}
}