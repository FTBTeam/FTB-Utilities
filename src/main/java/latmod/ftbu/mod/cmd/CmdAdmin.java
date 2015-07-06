package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUConfig;
import latmod.ftbu.mod.cmd.admin.*;

public class CmdAdmin extends CommandSubLM
{
	public CmdAdmin()
	{
		super(FTBUConfig.General.inst.commandAdmin, CommandLevel.OP);
		subCommands.put("player", new CmdAdminPlayer());
		subCommands.put("reload", new CmdAdminReload());
		subCommands.put("setitemname", new CmdAdminSetItemName());
		subCommands.put("displayitem", new CmdAdminDisplayItem());
		subCommands.put("getdim", new CmdAdminGetDim());
		subCommands.put("invsee", new CmdAdminInvsee());
		
		subCommands.put("setwarp", new CmdAdminSetWarp());
		subCommands.put("worldborder", new CmdAdminWorldBorder());
		subCommands.put("spawnarea", new CmdAdminSpawnArea());
		subCommands.put("unclaim", new CmdAdminUnclaim());
	}
}