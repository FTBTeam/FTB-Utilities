package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;

public class CmdFTBUNotify extends SubCommand
{
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[] { "off", "screen", "chat" };
		return null;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayer p = CommandLM.getLMPlayer(ics);
		CommandLM.checkArgs(args, 1);
		p.notify = 0;
		if(args[0].equals("screen")) p.notify = 1;
		else if(args[0].equals("chat")) p.notify = 2;
		p.sendUpdate(null, true);
		return CommandLM.FINE + "Notifications set to '" + getTabStrings(ics, args, 0)[p.notify] + "'";
	}
}