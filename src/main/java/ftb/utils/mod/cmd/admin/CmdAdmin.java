package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.mod.config.FTBUConfigCmd;

public class CmdAdmin extends CommandSubLM
{
	public CmdAdmin()
	{
		super(FTBUConfigCmd.name_admin.get(), CommandLevel.OP);
		add(new CmdRestart());
		add(new CmdInvsee());
		add(new CmdSetWarp());
		add(new CmdDelWarp());
		add(new CmdUnclaim());
		add(new CmdUnclaimAll());
		add(new CmdLoadedChunks());
		add(new CmdBackup());
		add(new CmdListFriends());
		add(new CmdUnloadAll());
		add(new CmdAdminHome());
	}
}