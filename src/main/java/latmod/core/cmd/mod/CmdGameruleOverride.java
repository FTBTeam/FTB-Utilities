package latmod.core.cmd.mod;

import net.minecraft.command.*;

public class CmdGameruleOverride extends CommandGameRule
{
	public final int enabled;
	
	public CmdGameruleOverride(int e)
	{ enabled = e; }
	
	public final int getRequiredPermissionLevel()
	{ return (enabled == 2) ? 2 : ((enabled == 1) ? 0 : 5); }
	
	public final boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return enabled > 0; }
}