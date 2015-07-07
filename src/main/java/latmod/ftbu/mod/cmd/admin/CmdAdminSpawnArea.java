package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUConfig;
import net.minecraft.command.ICommandSender;

public class CmdAdminSpawnArea extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 2);
		
		if(args[0].equals("safe"))
		{
			FTBUConfig.General.inst.safeSpawn = args[1].equals("true");
			FTBUConfig.General.save();
			return "SafeSpawn set to: "+ FTBUConfig.General.inst.safeSpawn;
		}
		else if(args[0].equals("pvp"))
		{
			FTBUConfig.General.inst.spawnPVP = args[1].equals("true");
			FTBUConfig.General.save();
			return "SpawnPVP set to: "+ FTBUConfig.General.inst.spawnPVP;
		}
		
		return "Subcommands: safe, pvp";
	}
}