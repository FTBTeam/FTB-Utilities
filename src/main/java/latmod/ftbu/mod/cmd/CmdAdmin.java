package latmod.ftbu.mod.cmd;

import ftb.lib.cmd.*;
import latmod.ftbu.mod.cmd.admin.*;
import latmod.ftbu.mod.config.FTBUConfigCmd;

public class CmdAdmin extends CommandSubLM
{
	public CmdAdmin()
	{
		super(FTBUConfigCmd.commandNameAdmin.get(), CommandLevel.OP);
		add(new CmdAdminPlayer("player"));
		add(new CmdAdminReload("reload"));
		add(new CmdAdminRestart("restart"));
		add(new CmdAdminSetItemName("setitemname"));
		if(FTBUConfigCmd.admin_invsee.get()) add(new CmdAdminInvsee("invsee"));
		if(FTBUConfigCmd.admin_warps.get())
		{
			add(new CmdAdminSetWarp("setwarp"));
			add(new CmdAdminDelWarp("delwarp"));
		}
		if(FTBUConfigCmd.admin_world_border.get()) add(new CmdAdminWorldBorder("worldborder"));
		if(FTBUConfigCmd.admin_edit_chunks.get())
		{
			add(new CmdAdminUnclaim("unclaim"));
			add(new CmdAdminUnclaimAll("unclaim_all"));
			add(new CmdAdminChunks("chunks"));
		}
		if(FTBUConfigCmd.admin_backup.get()) add(new CmdAdminBackup("backup"));
	}
}