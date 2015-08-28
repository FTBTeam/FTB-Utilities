package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.SubCommand;
import latmod.ftbu.mod.FTBUFinals;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBUVersion extends SubCommand //TODO: Remove
{
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{ return new ChatComponentText("Current version: " + FTBUFinals.VERSION); }
}