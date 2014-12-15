package latmod.core.cmd.mod;

import latmod.core.cmd.CommandLevel;
import net.minecraft.command.*;

public class CmdGamemodeOverride extends CommandGameMode
{
	public final CommandLevel level;
	
	public CmdGamemodeOverride(CommandLevel l)
	{ level = l; }
	
	public final int getRequiredPermissionLevel()
	{ return level.isOP() ? 2 : (level.isEnabled() ? 0 : 5); }
	
	public final boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return level.isEnabled(); }
}