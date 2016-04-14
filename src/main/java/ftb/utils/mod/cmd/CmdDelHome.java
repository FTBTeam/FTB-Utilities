package ftb.utils.mod.cmd;

import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
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
		if(i == 0) return LMPlayerServer.get(ics).homes.list();
		return null;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		LMPlayerServer p = LMPlayerServer.get(ics);
		checkArgs(args, 1);
		
		if(p.homes.set(args[0], null)) return FTBULang.home_del.chatComponent(args[0]);
		return error(FTBULang.home_not_set.chatComponent(args[0]));
	}
}