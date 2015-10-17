package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CmdAdminGetDim extends CommandLM
{
	public CmdAdminGetDim(String s)
	{ super(s, CommandLevel.OP); }

	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		int i = parseInt(ics, args[0]);
		WorldServer w = DimensionManager.getWorld(i);
		if(w == null) return error(new ChatComponentText("Invalid/Unloaded DimensionID!"));
		return new ChatComponentText("Dimension " + i + " name is '" + w.provider.getDimensionName() + "'");
	}
}