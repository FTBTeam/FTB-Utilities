package latmod.core.mod.cmd;

import latmod.core.LatCoreMC;
import latmod.core.mod.LMPlayer;
import net.minecraft.command.*;

public class CmdSetSkin extends CommandBaseLC
{
	public CmdSetSkin(int e)
	{ super(e); }
	
	public String getCommandName() 
	{ return "setskin"; }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/setskin <url | null>"; }
	
	public void processCommand(ICommandSender ics, String[] args)
	{
		if(args != null && args.length > 0)
		{
			LMPlayer p;
			
			if(args.length > 1 && enabled == 2)
				p = LMPlayer.getPlayer(args[1]);
			else p = LMPlayer.getPlayer(ics);
			
			if(p == null) throw new PlayerNotFoundException();
			
			p.customSkin = args[0].trim();
			if(p.customSkin.length() == 0 || p.customSkin.equals("null"))
				p.customSkin = null;
			
			p.sendUpdate("CustomSkin");
			
			LatCoreMC.printChat(ics, "Custom skin changed to " + p.customSkin);
		}
		else LatCoreMC.printChat(ics, getCommandUsage(ics));
	}
}