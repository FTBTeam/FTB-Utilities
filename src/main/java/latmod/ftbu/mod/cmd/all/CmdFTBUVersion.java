package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUFinals;
import net.minecraft.command.ICommandSender;

public class CmdFTBUVersion extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{ return CommandLM.FINE + "Current version: " + FTBUFinals.VERSION; }
}