package latmod.latcore.cmd;

import latmod.core.*;
import net.minecraft.command.*;
import net.minecraft.util.EnumChatFormatting;

public class CmdRealNick extends CommandBaseLC
{
	public CmdRealNick(int e)
	{ super("realnick", e); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/realnick <player>"; }
	
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
	
	public Boolean isUsername(String[] args, int i)
	{ return (i == 0) ? false : null; }
}