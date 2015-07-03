package latmod.ftbu.core.cmd;

import net.minecraft.command.CommandException;

public class FeatureDisabledException extends CommandException
{
	private static final long serialVersionUID = 1L;
	
	public FeatureDisabledException()
	{ super("commands.lmdisabled", new Object[0]); }
}