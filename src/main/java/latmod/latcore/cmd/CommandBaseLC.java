package latmod.latcore.cmd;

import latmod.core.cmd.CommandLM;
import net.minecraft.command.ICommandSender;

public abstract class CommandBaseLC extends CommandLM
{
	public final int enabled;
	
	public CommandBaseLC(String s, int e)
	{ super(s); enabled = e; }
	
	public final int getRequiredPermissionLevel()
	{ return (enabled == 2) ? 2 : ((enabled == 1) ? 0 : 5); }
	
	public final boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return enabled > 0; }
}