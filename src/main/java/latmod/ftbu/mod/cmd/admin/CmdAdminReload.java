package latmod.ftbu.mod.cmd.admin;

import ftb.lib.mod.FTBLibMod;
import latmod.ftbu.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class CmdAdminReload extends CommandLM
{
	public CmdAdminReload(String s)
	{ super(s, CommandLevel.OP); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{ FTBLibMod.reload(ics, true); return null; }
}