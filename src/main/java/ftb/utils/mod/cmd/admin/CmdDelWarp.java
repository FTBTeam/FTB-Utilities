package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMWorldServer;
import net.minecraft.command.*;

import java.util.List;

public class CmdDelWarp extends CommandLM
{
	public CmdDelWarp()
	{ super("delwarp", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		if(args.length == 1) return getListOfStringsFromIterableMatchingLastWord(args, LMWorldServer.inst.warps.list());
		return null;
	}
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		if(LMWorldServer.inst.warps.set(args[0], null)) FTBULang.warp_del.printChat(ics, args[0]);
		else FTBULang.warp_not_set.commandError(args[0]);
	}
}