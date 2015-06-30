package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUConfig;
import net.minecraft.command.ICommandSender;

public class CmdAdminWorldBorder extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 1);
		
		if(args[0].equals("on"))
		{
			FTBUConfig.WorldBorder.inst.enabled = true;
			FTBUConfig.WorldBorder.save();
			return CommandLM.FINE + "World border enabled";
		}
		else if(args[0].equals("off"))
		{
			FTBUConfig.WorldBorder.inst.enabled = false;
			FTBUConfig.WorldBorder.save();
			return CommandLM.FINE + "World border disabled";
		}
		
		CommandLM.checkArgs(args, 2);
		
		int dim = CommandLM.parseInt(ics, args[0]);
		int dist = CommandLM.parseInt(ics, args[1]);
		
		FTBUConfig.WorldBorder.inst.setWorldBorder(dim, dist);
		FTBUConfig.WorldBorder.save();
		return CommandLM.FINE + "World border for dimension " + dim + " set to " + dist;
	}
}