package latmod.ftbu.mod.cmd.admin;

import java.io.File;

import ftb.lib.FTBLib;
import ftb.lib.cmd.*;
import latmod.ftbu.mod.config.FTBUConfigCmd;
import latmod.lib.LMFileUtils;
import net.minecraft.command.*;
import net.minecraft.util.IChatComponent;

public class CmdRestart extends CommandLM
{
	public CmdRestart()
	{ super(FTBUConfigCmd.name_restart.get(), CommandLevel.OP); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{ restart(); return null; }
	
	public static void restart()
	{
		LMFileUtils.newFile(new File(FTBLib.folderMinecraft, "autostart.stamp"));
		FTBLib.getServer().initiateShutdown();
	}
}