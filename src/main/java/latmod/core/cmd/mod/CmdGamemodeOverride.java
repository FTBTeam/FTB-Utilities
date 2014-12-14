package latmod.core.cmd.mod;

import net.minecraft.command.*;

public class CmdGamemodeOverride extends CommandGameMode
{
	public final int enabled;
	
	public CmdGamemodeOverride(int e)
	{ enabled = e; }
	
	public final int getRequiredPermissionLevel()
	{ return (enabled == 2) ? 2 : ((enabled == 1) ? 0 : 5); }
	
	public final boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return enabled > 0; }
}