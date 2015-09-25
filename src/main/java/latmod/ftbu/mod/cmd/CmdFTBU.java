package latmod.ftbu.mod.cmd;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.cmd.all.*;
import latmod.ftbu.mod.config.FTBUConfig;

public class CmdFTBU extends CommandSubLM
{
	public CmdFTBU()
	{
		super(FTBUConfig.general.commandFTBU, CommandLevel.ALL);
		add(new CmdFTBUVersion("version"));
		add(new CmdFTBUFriends("friends"));
		add(new CmdFTBUPlayerID("ID"));
		add(new CmdFTBURestartTimer("restartTimer"));
		add(new CmdFTBUBackupTimer("backupTimer"));
	}
}