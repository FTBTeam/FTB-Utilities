package latmod.core.mod.cmd;

import latmod.core.cmd.*;
import net.minecraft.command.ICommandSender;

public abstract class CommandBaseLC extends CommandLM
{
	public final CommandLevel level;
	
	public CommandBaseLC(String s, CommandLevel l)
	{ super(s); level = l; }
	
	public final int getRequiredPermissionLevel()
	{ return level.requiredPermsLevel(); }
	
	public final boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return super.canCommandSenderUseCommand(ics); }
}