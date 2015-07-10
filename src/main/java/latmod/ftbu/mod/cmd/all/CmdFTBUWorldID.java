package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMWorldServer;
import net.minecraft.command.ICommandSender;

public class CmdFTBUWorldID extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{ return CommandLM.FINE + "WorldID: " + LMWorldServer.inst.worldIDS; }
}