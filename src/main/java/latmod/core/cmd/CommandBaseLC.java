package latmod.core.cmd;

import net.minecraft.command.ICommandSender;

public abstract class CommandBaseLC extends CommandLM
{
	public final CommandLevel level;
	
	public CommandBaseLC(String s, CommandLevel l)
	{ super(s); level = l; }
	
	public int getRequiredPermissionLevel()
	{ return level.requiredPermsLevel(); }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return super.canCommandSenderUseCommand(ics); }
}