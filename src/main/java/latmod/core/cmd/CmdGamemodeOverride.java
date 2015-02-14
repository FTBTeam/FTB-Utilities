package latmod.core.cmd;

import net.minecraft.command.CommandGameMode;

public class CmdGamemodeOverride extends CommandGameMode
{
	public static CommandLevel commandLevel = CommandLevel.OP;
	
	public final int getRequiredPermissionLevel()
	{ return commandLevel.requiredPermsLevel(); }
}