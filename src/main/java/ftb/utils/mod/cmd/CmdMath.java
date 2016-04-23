package ftb.utils.mod.cmd;

import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import latmod.lib.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class CmdMath extends CommandLM
{
	private static Boolean hasEngine = null;
	private static ScriptEngine engine = null;
	
	public CmdMath()
	{ super("math", CommandLevel.ALL); }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
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
				ics.addChatMessage(new ChatComponentText(String.valueOf(o)));
				return;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			error("JavaScript Engine not found!");
		}
		
		error("Error");
	}
}