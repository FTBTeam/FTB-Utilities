package latmod.core.mod;

import net.minecraft.command.*;

public class LCCommand extends CommandBase
{
	public String getCommandName()
	{
		return "latcore";
	}

	public String getCommandUsage(ICommandSender ics)
	{ return "/latcore <subcommand>"; }

	public void processCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
		{
		}
		else if(args != null)
		{
		}
	}
}