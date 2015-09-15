package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.LMStringUtils;
import latmod.ftbu.mod.backups.Backups;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBUBackupTimer extends CommandLM
{
	public CmdFTBUBackupTimer(String s)
	{ super(s, CommandLevel.ALL); }

	public IChatComponent onCommand(ICommandSender ics, String[] args) //LANG
	{
		if(!FTBUConfig.backups.enabled) throw new FeatureDisabledException();
		return new ChatComponentText("Time left until next backup: " + LMStringUtils.formatTime(Backups.getSecondsUntilNextBackup(), false));
	}
}
