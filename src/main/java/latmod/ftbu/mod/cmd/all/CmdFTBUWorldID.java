package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMWorld;
import net.minecraft.command.ICommandSender;

public class CmdFTBUWorldID extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{ return CommandLM.FINE + "WorldID: " + LMWorld.server.worldIDS; }
}