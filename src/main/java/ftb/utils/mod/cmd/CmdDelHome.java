package ftb.utils.mod.cmd;

import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CmdDelHome extends CommandLM
{
	public CmdDelHome()
	{ super("delhome", CommandLevel.ALL); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		if(args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, LMPlayerServer.get(ics).homes.list());
		}
		
		return null;
	}
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		LMPlayerServer p = LMPlayerServer.get(ics);
		checkArgs(args, 1);
		
		if(p.homes.set(args[0], null)) FTBULang.home_del.printChat(ics, args[0]);
		else FTBULang.home_not_set.commandError(args[0]);
	}
}