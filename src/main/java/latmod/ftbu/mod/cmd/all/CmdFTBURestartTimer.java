package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LMStringUtils;
import latmod.ftbu.mod.FTBUTickHandler;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBURestartTimer extends SubCommand
{
	public IChatComponent onCommand(ICommandSender ics, String[] args) //LANG
	{
		if(FTBUConfig.general.restartTimer <= 0D)
			throw new FeatureDisabledException();
		return new ChatComponentText("Time left until next restart: " + LMStringUtils.formatTime(FTBUTickHandler.getSecondsUntilRestart(), false));
	}
}
