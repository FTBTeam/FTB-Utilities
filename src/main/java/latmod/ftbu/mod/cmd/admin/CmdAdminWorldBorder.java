package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;

public class CmdAdminWorldBorder extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		/*
		CommandLM.checkArgs(args, 1);
		
		if(args[0].equals("square"))
		{
			EnkiToolsConfig.get().world.worldBorderSquare = true;
			EnkiToolsConfig.saveConfig();
			return CommandLM.FINE + "World border is now a square";
		}
		if(args[0].equals("round"))
		{
			EnkiToolsConfig.get().world.worldBorderSquare = false;
			EnkiToolsConfig.saveConfig();
			return CommandLM.FINE + "World border is now round";
		}
		
		CommandLM.checkArgs(args, 2);
		
		int dim = CommandLM.parseInt(ics, args[0]);
		int dist = CommandLM.parseInt(ics, args[1]);
		
		EnkiToolsConfig.get().world.worldBorder.put(dim, dist);
		EnkiToolsConfig.saveConfig();
		return CommandLM.FINE + "World border for dimension " + dim + " set to " + dist;
		*/
		
		return "Unimplemented";
	}
}