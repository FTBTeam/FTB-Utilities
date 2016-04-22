package ftb.utils.cmd;

import ftb.lib.api.cmd.*;
import latmod.lib.LMStringUtils;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.script.*;

public class CmdMath extends CommandLM
{
	private static Boolean hasEngine = null;
	private static ScriptEngine engine = null;
	
	public CmdMath()
	{ super("math", CommandLevel.ALL); }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(hasEngine == null)
		{
			hasEngine = Boolean.FALSE;
			
			try
			{
				engine = new ScriptEngineManager().getEngineByName("JavaScript");
				if(engine != null) hasEngine = Boolean.TRUE;
			}
			catch(Exception e) { e.printStackTrace(); }
		}
		
		if(hasEngine == Boolean.TRUE)
		{
			try
			{
				String s = LMStringUtils.unsplit(args, " ").trim();
				Object o = engine.eval(s);
				ics.addChatMessage(new TextComponentString(String.valueOf(o)));
				return;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			throw new RawCommandException("JavaScript Engine not found!");
		}
		
		throw new RawCommandException("Error");
	}
}