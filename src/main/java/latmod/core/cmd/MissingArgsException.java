package latmod.core.cmd;

import net.minecraft.command.CommandException;

public class MissingArgsException extends CommandException
{
	private static final long serialVersionUID = 1L;
	
	public MissingArgsException()
	{ super("commands.lmmissingargs", new Object[0]); }
}