package latmod.core.mod.cmd;

import latmod.core.cmd.CommandLevel;
import net.minecraft.command.*;

public class CmdGamemodeOverride extends CommandGameMode
{
	public final CommandLevel level;
	
	public CmdGamemodeOverride(CommandLevel l)
	{ level = l; }
	
	public final int getRequiredPermissionLevel()
	{ return level.requiredPermsLevel(); }
}