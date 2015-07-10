package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMPlayerServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdTplast extends CommandLM
{
	public CmdTplast()
	{ super("tpl", CommandLevel.OP); }
	
	public NameType getUsername(String[] args, int i)
	{ if(i == 0 || i == 1) return NameType.OFF; return NameType.NONE; }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		
		EntityPlayerMP who;
		LMPlayerServer to;
		
		if(args.length == 1)
		{
			who = getCommandSenderAsPlayer(ics);
			to = getLMPlayer(args[0]);
		}
		else
		{
			who = getPlayer(ics, args[0]);
			to = getLMPlayer(args[1]);
		}
		
		EntityPos p = to.getLastPos();
		if(p == null) return "No last position!";
		LMDimHelper.teleportPlayer(who, p);
		return FINE + "Teleported to " + to.getName() + "!";
	}
}