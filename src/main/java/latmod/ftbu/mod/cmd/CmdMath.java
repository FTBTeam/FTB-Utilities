package latmod.ftbu.mod.cmd;

import javax.script.*;

import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.util.CommandFTBU;
import latmod.lib.LMStringUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdMath extends CommandFTBU
{
	public CmdMath()
	{ super("math", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		
		try
		{
			String s = LMStringUtils.unsplit(args, " ").trim();
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			if(engine == null) return error(new ChatComponentText("Error"));
			Object o = engine.eval(s);
			return new ChatComponentText(String.valueOf(o));
		}
		catch(Exception e) { e.printStackTrace(); }
		
		return error(new ChatComponentText("Error"));
	}
}