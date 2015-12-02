package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.*;
import latmod.ftbu.mod.config.FTBUConfigCmd;
import net.minecraft.command.ICommandSender;

public class CmdAdmin extends CommandSubLM
{
	public CmdAdmin()
	{
		super(FTBUConfigCmd.name_admin.get(), CommandLevel.OP);
		add(new CmdPlayerLM());
		add(new CmdRestart());
		add(new CmdSetItemName());
		add(new CmdInvsee());
		add(new CmdSetWarp());
		add(new CmdDelWarp());
		add(new CmdWorldBorder());
		add(new CmdUnclaim());
		add(new CmdUnclaimAll());
		add(new CmdLoadedChunks());
		add(new CmdBackup());
	}
	
	public String getCommandUsage(ICommandSender ics)
	{
		return super.getCommandUsage(ics);
	}
}