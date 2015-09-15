package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUFinals;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBUVersion extends CommandLM //TODO: Remove
{
	public CmdFTBUVersion(String s)
	{ super(s, CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{ return new ChatComponentText("Current version: " + FTBUFinals.VERSION); }
}