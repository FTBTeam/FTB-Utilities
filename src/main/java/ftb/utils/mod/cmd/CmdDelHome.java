package ftb.utils.mod.cmd;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.LMPlayerMP;
import ftb.utils.mod.FTBU;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.*;
import net.minecraft.util.IChatComponent;

public class CmdDelHome extends CommandLM
{
	public CmdDelHome()
	{ super("delhome", CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{
		if(i == 0) return FTBUPlayerDataMP.get(LMPlayerMP.get(ics)).homes.list();
		return null;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		LMPlayerMP p = LMPlayerMP.get(ics);
		checkArgs(args, 1);
		
		if(FTBUPlayerDataMP.get(p).homes.set(args[0], null)) return FTBU.mod.chatComponent("cmd.home_del", args[0]);
		return error(FTBU.mod.chatComponent("cmd.home_not_set", args[0]));
	}
}