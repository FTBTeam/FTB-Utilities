package latmod.core.mod.cmd;

import latmod.core.cmd.CommandLevel;
import net.minecraft.command.CommandGameRule;

public class CmdGameruleOverride extends CommandGameRule
{
	public final CommandLevel level;
	
	public CmdGameruleOverride(CommandLevel l)
	{ level = l; }
	
	public final int getRequiredPermissionLevel()
	{ return level.requiredPermsLevel(); }
}