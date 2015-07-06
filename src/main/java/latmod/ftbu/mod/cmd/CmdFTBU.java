package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUConfig;
import latmod.ftbu.mod.cmd.all.*;

public class CmdFTBU extends CommandSubLM
{
	public CmdFTBU()
	{
		super(FTBUConfig.General.inst.commandFTBU, CommandLevel.ALL);
		subCommands.put("version", new CmdFTBUVersion());
		subCommands.put("friends", new CmdFTBUFriends());
		subCommands.put("uuid", new CmdFTBUUUID());
		subCommands.put("playerID", new CmdFTBUPlayerID());
	}
}