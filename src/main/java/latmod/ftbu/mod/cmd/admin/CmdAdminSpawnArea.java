package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.config.*;
import net.minecraft.command.ICommandSender;

public class CmdAdminSpawnArea extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 2);
		
		if(args[0].equals("safe"))
		{
			FTBUConfig.general.safeSpawn = args[1].equals("true");
			ConfigGeneral.save();
			return "SafeSpawn set to: "+ FTBUConfig.general.safeSpawn;
		}
		else if(args[0].equals("pvp"))
		{
			FTBUConfig.general.spawnPVP = args[1].equals("true");
			ConfigGeneral.save();
			return "SpawnPVP set to: "+ FTBUConfig.general.spawnPVP;
		}
		
		return "Subcommands: safe, pvp";
	}
}