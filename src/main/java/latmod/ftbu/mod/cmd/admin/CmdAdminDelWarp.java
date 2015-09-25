package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class CmdAdminDelWarp extends CommandLM
{
	public CmdAdminDelWarp(String s)
	{ super(s, CommandLevel.OP); }

	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		return null;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		throw new FeatureDisabledException();
		/*
		 * checkArgs(args, 1);
			
			if(EnkiData.Warps.remWarp(args[0]))
				return FINE + "Warp '" + args[0] + "' removed!";
			return "Warp '" + args[0] + "' doesn't exist!";
		 */
	}
}