package latmod.core.mod.cmd;

import java.util.List;

import latmod.core.LatCoreMC;
import latmod.core.mod.LMPlayer;
import net.minecraft.command.*;

public abstract class CommandLM extends CommandBase
{
	public final String commandName;
	
	public CommandLM(String s)
	{ commandName = s; }
	
	public final String getCommandName()
	{ return commandName; }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/" + commandName; }
	
	public final void processCommand(ICommandSender ics, String[] args)
	{
		if(args == null) args = new String[0];
		onCommand(ics, args);
	}
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, getCommandUsage(ics)); }
	
	public abstract void onCommand(ICommandSender ics, String[] args);
	
	@SuppressWarnings("all")
	public List addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		int un = isUsername(args.length - 1);
		
		if(un == 1)
			return getListOfStringsMatchingLastWord(args, LMPlayer.getAllDisplayNames(true));
		if(un == 2)
			return getListOfStringsMatchingLastWord(args, LMPlayer.getAllDisplayNames(false));
		
		return null;
	}
	
	/**
	 * 0 - none
	 * 1 - online
	 * 2 - all */
	public int isUsername(int i)
	{ return 0; }
}