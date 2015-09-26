package latmod.ftbu.mod.cmd.all;

import latmod.core.util.LMStringUtils;
import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.FTBUTicks;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBURestartTimer extends CommandLM
{
	public CmdFTBURestartTimer(String s)
	{ super(s, CommandLevel.ALL); }

	public IChatComponent onCommand(ICommandSender ics, String[] args) //LANG
	{
		if(FTBUConfig.general.restartTimer <= 0D)
			throw new FeatureDisabledException();
		return new ChatComponentText("Time left until next restart: " + LMStringUtils.getTimeString(FTBUTicks.getSecondsUntilRestart()));
	}
}
