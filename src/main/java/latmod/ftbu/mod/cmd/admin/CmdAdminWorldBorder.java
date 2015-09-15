package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.config.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdAdminWorldBorder extends CommandLM //TODO: Remove
{
	public CmdAdminWorldBorder(String s)
	{ super(s, CommandLevel.OP); }

	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		
		if(args[0].equals("on"))
		{
			FTBUConfig.world_border.enabled = true;
			ConfigWorldBorder.save();
			return new ChatComponentText("World border enabled");
		}
		else if(args[0].equals("off"))
		{
			FTBUConfig.world_border.enabled = false;
			ConfigWorldBorder.save();
			return new ChatComponentText("World border disabled");
		}
		
		checkArgs(args, 2);
		
		int dim = parseInt(ics, args[0]);
		int dist = parseInt(ics, args[1]);
		
		FTBUConfig.world_border.setWorldBorder(dim, dist);
		ConfigWorldBorder.save();
		return new ChatComponentText("World border for dimension " + dim + " set to " + dist);
	}
}