package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.mod.FTBUTickHandler;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;

public class CmdFTBURestartTimer extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(FTBUConfig.general.restartTimer <= 0D)
			throw new FeatureDisabledException();
		return CommandLM.FINE + "Time left until next restart: " + LatCore.formatTime(FTBUTickHandler.getSecondsUntilRestart(), false);
	}
}
