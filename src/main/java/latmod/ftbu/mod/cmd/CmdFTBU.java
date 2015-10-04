package latmod.ftbu.mod.cmd;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.cmd.all.*;
import latmod.ftbu.mod.config.FTBUConfigGeneral;

public class CmdFTBU extends CommandSubLM
{
	public CmdFTBU()
	{
		super(FTBUConfigGeneral.commandFTBU.get(), CommandLevel.ALL);
		add(new CmdFTBUVersion("version"));
		add(new CmdFTBUPlayerID("ID"));
		add(new CmdFTBURestartTimer("restartTimer"));
		add(new CmdFTBUBackupTimer("backupTimer"));
		add(new CmdFTBUTopDeaths("deaths"));
	}
}