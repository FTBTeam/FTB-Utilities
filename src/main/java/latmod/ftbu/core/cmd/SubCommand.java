package latmod.ftbu.core.cmd;

import net.minecraft.command.ICommandSender;

public abstract class SubCommand
{
	public abstract String onCommand(ICommandSender ics, String[] args);
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		return null;
	}
	
	public NameType getUsername(String[] args, int i)
	{
		return NameType.NONE;
	}
	
	public static String[] trimArgs(String[] args)
	{
		if(args == null || args.length == 0) return new String[0];
		String[] args1 = new String[args.length - 1];
		for(int i = 0; i < args1.length; i++)
			args1[i] = args[i + 1];
		return args1;
	}
}