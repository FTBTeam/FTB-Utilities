package ftb.utils.mod.cmd;

import ftb.lib.api.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBU;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.*;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CmdDelHome extends CommandLM
{
	public CmdDelHome()
	{ super("delhome", CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args, BlockPos pos)
	{
		if(args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUPlayerDataMP.get(ForgeWorldMP.inst.getPlayer(ics)).homes.list());
		}
		return null;
	}
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		ForgePlayerMP p = ForgePlayerMP.get(ics);
		checkArgs(args, 1);
		
		if(FTBUPlayerDataMP.get(p).homes.set(args[0], null))
			ics.addChatMessage(FTBU.mod.chatComponent("cmd.home_del", args[0]));
		
		throw new CommandException("ftbu.cmd.home_not_set", args[0]);
	}
}