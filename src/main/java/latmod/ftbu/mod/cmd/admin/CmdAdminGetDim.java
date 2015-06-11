package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CmdAdminGetDim extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 1);
		int i = CommandLM.parseInt(ics, args[0]);
		WorldServer w = DimensionManager.getWorld(i);
		if(w == null) return "Invalid DimensionID!";
		return CommandLM.FINE + "Dimension " + i + " name is '" + w.provider.getDimensionName() + "'";
	}
}