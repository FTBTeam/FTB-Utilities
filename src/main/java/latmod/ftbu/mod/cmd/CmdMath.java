package latmod.ftbu.mod.cmd;

import javax.script.*;

import latmod.core.util.LMStringUtils;
import latmod.ftbu.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdMath extends CommandLM
{
	public CmdMath()
	{ super("math", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		
		try
		{
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("JavaScript");
			Object o = engine.eval(LMStringUtils.unsplit(args, " "));
			return new ChatComponentText(String.valueOf(o));
		}
		catch(Exception e) { }
		
		return error(new ChatComponentText("null"));
	}
}