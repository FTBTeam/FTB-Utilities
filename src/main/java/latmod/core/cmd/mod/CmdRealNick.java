package latmod.core.cmd.mod;

import latmod.core.*;
import latmod.core.cmd.CommandLevel;
import net.minecraft.command.*;
import net.minecraft.util.EnumChatFormatting;

public class CmdRealNick extends CommandBaseLC
{
	public CmdRealNick(CommandLevel l)
	{ super("realnick", l); }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return null; }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "<player>");
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args.length > 0)
		{
			LMPlayer p = LMPlayer.getPlayer(args[0]);
			
			if(p == null) throw new PlayerNotFoundException();
			
			LatCoreMC.printChat(ics, p.getDisplayName() + EnumChatFormatting.RESET + " is " + p.username);
		}
		
		return null;
	}
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 0) ? NameType.LM_OFF : NameType.NONE; }
}