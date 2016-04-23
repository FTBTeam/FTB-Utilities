package ftb.utils.mod.cmd.admin;

import ftb.lib.FTBLib;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import latmod.lib.LMFileUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.io.File;

public class CmdRestart extends CommandLM
{
	public CmdRestart()
	{ super("restart", CommandLevel.OP); }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		restart();
	}
	
	public static void restart()
	{
		LMFileUtils.newFile(new File(FTBLib.folderMinecraft, "autostart.stamp"));
		FTBLib.getServer().initiateShutdown();
	}
}