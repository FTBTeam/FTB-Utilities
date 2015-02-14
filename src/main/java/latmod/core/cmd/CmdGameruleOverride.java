package latmod.core.cmd;

import net.minecraft.command.CommandGameRule;

public class CmdGameruleOverride extends CommandGameRule
{
	public static CommandLevel commandLevel = CommandLevel.OP;
	
	public final int getRequiredPermissionLevel()
	{ return commandLevel.requiredPermsLevel(); }
}