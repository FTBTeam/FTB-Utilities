package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.cmd.all.*;
import latmod.ftbu.mod.config.FTBUConfig;

public class CmdFTBU extends CommandSubLM
{
	public CmdFTBU()
	{
		super(FTBUConfig.general.commandFTBU, CommandLevel.ALL);
		subCommands.put("version", new CmdFTBUVersion());
		subCommands.put("friends", new CmdFTBUFriends());
		subCommands.put("uuid", new CmdFTBUUUID());
		subCommands.put("playerID", new CmdFTBUPlayerID());
		subCommands.put("restartTimer", new CmdFTBURestartTimer());
		subCommands.put("backupTimer", new CmdFTBUBackupTimer());
	}
}