package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.backups.Backups;
import net.minecraft.command.ICommandSender;

public class CmdFTBUBackupTimer extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(FTBUConfig.Backups.inst.backupsToKeep <= 0) throw new FeatureDisabledException();
		return CommandLM.FINE + "Time left until next backup: " + LatCore.formatTime(Backups.getSecondsUntilNextBackup(), false);
	}
}
