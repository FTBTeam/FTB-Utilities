package latmod.core.mod.cmd;

import latmod.core.cmd.*;
import net.minecraft.command.ICommandSender;

public abstract class CommandBaseLC extends CommandLM
{
	public final CommandLevel level;
	
	public CommandBaseLC(String s, CommandLevel l)
	{ super(s); level = l; }
	
	public final int getRequiredPermissionLevel()
	{ return level.isOP() ? 2 : (level.isEnabled() ? 0 : 5); }
	
	public final boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return level.isEnabled(); }
}