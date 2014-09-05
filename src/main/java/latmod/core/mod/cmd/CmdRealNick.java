package latmod.core.mod.cmd;

import latmod.core.LatCoreMC;
import latmod.core.mod.LMPlayer;
import net.minecraft.command.*;
import net.minecraft.util.EnumChatFormatting;

public class CmdRealNick extends CommandBaseLC
{
	public CmdRealNick(int e)
	{ super(e); }
	
	public String getCommandName() 
	{ return "realnick"; }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/realnick <player>"; }
	
	public void processCommand(ICommandSender ics, String[] args)
	{
		if(args != null && args.length > 0)
		{
			LMPlayer p = LMPlayer.getPlayer(args[0]);
			
			if(p == null) throw new PlayerNotFoundException();
			
			LatCoreMC.printChat(ics, p.getDisplayName() + EnumChatFormatting.RESET + "'s real username is " + p.username);
		}
	}
}