package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdTplast extends CommandLM
{
	public CmdTplast()
	{ super("tpl", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " [who] <to>"; }
	
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0 || i == 1; }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args.length == 3)
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			double x = func_110665_a(ics, ep.posX, args[0], -30000000, 30000000);
			double y = func_110665_a(ics, ep.posY, args[1], -30000000, 30000000);
			double z = func_110665_a(ics, ep.posZ, args[2], -30000000, 30000000);
			LMDimUtils.teleportEntity(ep, x, y, z, ep.dimension);
			return;
		}
		
		EntityPlayerMP who;
		LMPlayerServer to;
		
		if(args.length == 1)
		{
			who = getCommandSenderAsPlayer(ics);
			to = LMPlayerServer.get(args[0]);
		}
		else
		{
			who = getPlayer(ics, args[0]);
			to = LMPlayerServer.get(args[1]);
		}
		
		BlockDimPos p = to.getPos();
		if(p == null) error("No last position!"); //TODO: Lang
		else
		{
			LMDimUtils.teleportEntity(who, p);
			FTBULang.warp_tp.printChat(ics, to.getProfile().getName());
		}
	}
}