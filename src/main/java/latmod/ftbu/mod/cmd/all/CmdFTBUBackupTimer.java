package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LMStringUtils;
import latmod.ftbu.mod.backups.Backups;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;

public class CmdFTBUBackupTimer extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(!FTBUConfig.backups.enabled) throw new FeatureDisabledException();
		return CommandLM.FINE + "Time left until next backup: " + LMStringUtils.formatTime(Backups.getSecondsUntilNextBackup(), false);
	}
}
