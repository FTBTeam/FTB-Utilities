package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.SubCommand;
import net.minecraft.command.ICommandSender;

public class CmdAdminSpawnArea extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		/*
		if(args.length == 0)
			return CommandLM.FINE + "Curent spawn area size: " + EnkiToolsConfig.get().world.spawnDistance + (EnkiToolsConfig.get().world.spawnSquare ? " [Square]" : " [Round]");
		
		CommandLM.checkArgs(args, 1);
		
		if(args[1].equals("square"))
		{
			EnkiToolsConfig.get().world.spawnSquare = true;
			EnkiToolsConfig.saveConfig();
			return CommandLM.FINE + "Spawn area is now a square";
		}
		if(args[1].equals("round"))
		{
			EnkiToolsConfig.get().world.spawnSquare = false;
			EnkiToolsConfig.saveConfig();
			return CommandLM.FINE + "Spawn area is now round";
		}
		else
		{
			int dist = CommandLM.parseInt(ics, args[0]);
			EnkiToolsConfig.get().world.spawnDistance = dist;
			EnkiToolsConfig.saveConfig();
			return CommandLM.FINE + "Spawn distance set to " + dist;
		}
		*/
		
		return "Unimplemented!";
	}
}