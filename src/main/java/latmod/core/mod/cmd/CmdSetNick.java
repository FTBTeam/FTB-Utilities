package latmod.core.mod.cmd;

import latmod.core.LatCoreMC;
import latmod.core.mod.LMPlayer;
import net.minecraft.command.*;

public class CmdSetNick extends CommandBaseLC
{
	public CmdSetNick(int e)
	{ super(e); }
	
	public String getCommandName() 
	{ return "setnick"; }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/setnick <nick | null>"; }
	
	public void processCommand(ICommandSender ics, String[] args)
	{
		if(args != null && args.length > 0)
		{
			LMPlayer p;
			
			if(args.length > 1 && enabled == 2)
				p = LMPlayer.getPlayer(args[1]);
			else p = LMPlayer.getPlayer(ics);
			
			if(p == null) throw new PlayerNotFoundException();
			
			p.setCustomName(args[0].trim());
			p.getPlayer(ics.getEntityWorld()).refreshDisplayName();
			p.sendUpdate(ics.getEntityWorld(), "CustomName");
			
			LatCoreMC.printChat(ics, "Custom nickname changed to " + p.getDisplayName());
		}
		else LatCoreMC.printChat(ics, getCommandUsage(ics));
	}
}