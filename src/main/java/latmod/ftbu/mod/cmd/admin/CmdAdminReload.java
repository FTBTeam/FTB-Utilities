package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.CommandLevel;
import ftb.lib.mod.FTBLibMod;
import latmod.ftbu.util.CommandFTBU;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class CmdAdminReload extends CommandFTBU
{
	public CmdAdminReload(String s)
	{ super(s, CommandLevel.OP); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{ FTBLibMod.reload(ics, true); return null; }
}