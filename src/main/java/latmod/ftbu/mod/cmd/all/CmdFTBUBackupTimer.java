package latmod.ftbu.mod.cmd.all;

import latmod.core.util.LMStringUtils;
import latmod.ftbu.backups.Backups;
import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.config.FTBUConfigBackups;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBUBackupTimer extends CommandLM
{
	public CmdFTBUBackupTimer(String s)
	{ super(s, CommandLevel.ALL); }

	public IChatComponent onCommand(ICommandSender ics, String[] args) //LANG
	{
		if(!FTBUConfigBackups.enabled.get()) throw new FeatureDisabledException();
		return new ChatComponentText("Time left until next backup: " + LMStringUtils.getTimeString(Backups.getSecondsUntilNextBackup()));
	}
}
