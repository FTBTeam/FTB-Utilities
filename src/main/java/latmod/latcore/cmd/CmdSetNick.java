package latmod.latcore.cmd;

import latmod.core.*;
import net.minecraft.command.*;

public class CmdSetNick extends CommandBaseLC
{
	public CmdSetNick(int e)
	{ super("setnick", e); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/setnick <nick | null>"; }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args.length > 0)
		{
			LMPlayer p;
			
			if(args.length > 1 && enabled == 2)
				p = LMPlayer.getPlayer(args[1]);
			else p = LMPlayer.getPlayer(ics);
			
			if(p == null) throw new PlayerNotFoundException();
			
			p.setCustomName(args[0].trim());
			p.getPlayer().refreshDisplayName();
			p.sendUpdate(ics.getEntityWorld(), "CustomName");
			
			LatCoreMC.printChat(ics, "Custom nickname changed to " + p.getDisplayName());
		}
		
		return getCommandUsage(ics);
	}
}