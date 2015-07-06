package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.mod.*;
import net.minecraft.command.ICommandSender;


public class CmdRestartTimer extends CommandLM
{
	public CmdRestartTimer()
	{ super("restartTimer", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(FTBUConfig.General.inst.restartTimer <= 0D) return "Restart timer disabled!";
		return FINE + "Time left until next restart: " + LatCore.formatTime(FTBUTickHandler.getSecondsUntilRestart(), false);
	}
}