package mods.lm_core.mod;
import mods.lm_core.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CmdDebug extends CommandBase
{
	public String getCommandName()
	{ return "debugLM"; }

	public String getCommandUsage(ICommandSender ics)
	{ return "/debugLM [true / false]"; }
	
	public void processCommand(ICommandSender ics, String[] s)
	{
		LatCore.debug = !LatCore.debug;
		
		if(s != null && s.length == 1)
		LatCore.debug = s[0].trim().toLowerCase().equals("true");
		
		ics.sendChatToPlayer(ChatMessageComponent.createFromText("LatCore Debugging: " + LatCore.debug));
	}
}