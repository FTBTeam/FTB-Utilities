package mods.lm_core.mod;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CmdPlayerID extends CommandBase
{
	public String getCommandName()
	{ return "playerID"; }

	public String getCommandUsage(ICommandSender ics)
	{ return "/playerID [playerName]"; }
	
	public void processCommand(ICommandSender ics, String[] s)
	{
		String name = "";
		if(s != null && s.length == 1)
		name = s[0];
		else name = ics.getCommandSenderName();
		int id = -1;
		if(name != null && name.length() > 0) id = PlayerID.inst.get(name, false);
		if(id > 0) ics.sendChatToPlayer(ChatMessageComponent.createFromText("Player '" + name + "' ID is " + id));
		else ics.sendChatToPlayer(ChatMessageComponent.createFromText("Player '" + name + "' ID hasn't registred yet!"));
	}
}