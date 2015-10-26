package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.FTBUTicks;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.lib.LMStringUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBURestartTimer extends CommandLM
{
	public CmdFTBURestartTimer(String s)
	{ super(s, CommandLevel.ALL); }

	public IChatComponent onCommand(ICommandSender ics, String[] args) //LANG
	{
		if(FTBUConfigGeneral.restartTimer.get() <= 0F)
			throw new FeatureDisabledException();
		return new ChatComponentText("Time left until next restart: " + LMStringUtils.getTimeString(FTBUTicks.getSecondsUntilRestart() * 1000L));
	}
}
