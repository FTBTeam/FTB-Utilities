package latmod.ftbu.core.cmd;

import latmod.ftbu.core.util.*;
import net.minecraft.command.ICommandSender;

public class CommandSubLM extends CommandLM
{
	public final FastMap<String, SubCommand> subCommands;
	
	public CommandSubLM(String s, CommandLevel l)
	{
		super(s, l);
		subCommands = new FastMap<String, SubCommand>();
	}
	
	public String[] getSubcommands(ICommandSender ics)
	{ return subCommands.keys.toArray(new String[0]); }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		
		SubCommand cmd = subCommands.get(args[0]);
		
		if(cmd != null)
		{
			String[] s = cmd.getTabStrings(ics, SubCommand.trimArgs(args), i - 1);
			if(s != null && s.length > 0) return s;
		}
		
		return super.getTabStrings(ics, args, i);
	}
	
	public NameType getUsername(String[] args, int i)
	{
		if(i > 0 && args.length > 1)
		{
			SubCommand cmd = subCommands.get(args[0]);
			if(cmd != null)
				return cmd.getUsername(SubCommand.trimArgs(args), i - 1);
		}
		
		return NameType.NONE;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return FINE + "Subcommands: " + LatCore.strip(getTabStrings(ics, args, 0));
		SubCommand cmd = subCommands.get(args[0]);
		if(cmd != null) return cmd.onCommand(ics, SubCommand.trimArgs(args));
		return "Invalid subcommand '" + args[0] + "'!";
	}
}