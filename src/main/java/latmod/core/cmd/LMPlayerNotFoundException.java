package latmod.core.cmd;

import net.minecraft.command.CommandException;

public class LMPlayerNotFoundException extends CommandException
{
	private static final long serialVersionUID = 1L;
	
	public LMPlayerNotFoundException(String name)
	{ super("commands.lmpnotfound", new Object[]{ name }); }
}