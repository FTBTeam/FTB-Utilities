package latmod.latcore.cmd;

import latmod.core.*;
import net.minecraft.command.*;

public class CmdSetCape extends CommandBaseLC
{
	public CmdSetCape(int e)
	{ super("setcape", e); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/setcape <url | null>"; }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args.length > 0)
		{
			LMPlayer p;
			
			if(args.length > 1 && enabled == 2)
				p = LMPlayer.getPlayer(args[1]);
			else p = LMPlayer.getPlayer(ics);
			
			if(p == null) throw new PlayerNotFoundException();
			
			p.customCape = args[0].trim();
			if(p.customCape.length() == 0 || p.customCape.equals("null"))
				p.customCape = null;
			
			p.sendUpdate(ics.getEntityWorld(), "CustomCape");
			
			LatCoreMC.printChat(ics, "Custom cape changed to " + p.customCape);
		}
		
		return getCommandUsage(ics);
	}
}