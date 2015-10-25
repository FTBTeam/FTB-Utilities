package latmod.ftbu.mod.cmd;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.cmd.all.*;
import latmod.ftbu.mod.config.*;

public class CmdFTBU extends CommandSubLM
{
	public CmdFTBU()
	{
		super(FTBUConfigGeneral.commandFTBU.get(), CommandLevel.ALL);
		add(new CmdFTBUVersion("version"));
		if(FTBUConfigCmd.ftbu_playerID.get()) add(new CmdFTBUPlayerID("ID"));
		if(FTBUConfigCmd.ftbu_restart_timer.get()) add(new CmdFTBURestartTimer("restartTimer"));
		if(FTBUConfigCmd.ftbu_backup_timer.get()) add(new CmdFTBUBackupTimer("backupTimer"));
		if(FTBUConfigCmd.ftbu_tops.get()) add(new CmdFTBUTops("top"));
	}
}