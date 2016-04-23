package ftb.utils.mod.cmd.admin;

import ftb.lib.FTBLib;
import ftb.lib.api.cmd.*;
import latmod.lib.LMFileUtils;
import net.minecraft.command.*;

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