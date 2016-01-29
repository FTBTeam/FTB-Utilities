package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.config.FTBUConfigCmd;
import ftb.utils.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdTplast extends CommandLM
{
	public CmdTplast()
	{ super(FTBUConfigCmd.name_tplast.get(), CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " [who] <to>"; }
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0 || i == 1) ? Boolean.FALSE : null; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args.length == 3)
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			double x = func_110665_a(ics, ep.posX, args[0], -30000000, 30000000);
			double y = func_110665_a(ics, ep.posY, args[1], -30000000, 30000000);
			double z = func_110665_a(ics, ep.posZ, args[2], -30000000, 30000000);
			LMDimUtils.teleportPlayer(ep, x, y, z, ep.dimension);
			return null;
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
		if(p == null) return error(new ChatComponentText("No last position!"));
		LMDimUtils.teleportPlayer(who, p);
		return new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_tp", to.getProfile().getName());
	}
}