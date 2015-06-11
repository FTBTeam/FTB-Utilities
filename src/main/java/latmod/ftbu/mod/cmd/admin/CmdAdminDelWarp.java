package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.SubCommand;
import net.minecraft.command.ICommandSender;

public class CmdAdminDelWarp extends SubCommand
{
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		return null;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		/*
		 * checkArgs(args, 1);
			
			if(EnkiData.Warps.remWarp(args[0]))
				return FINE + "Warp '" + args[0] + "' removed!";
			return "Warp '" + args[0] + "' doesn't exist!";
		 */
		return "Unimplemented!";
	}
}