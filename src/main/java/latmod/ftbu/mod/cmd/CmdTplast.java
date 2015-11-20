package latmod.ftbu.mod.cmd;

import ftb.lib.*;
import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.CommandFTBU;
import latmod.ftbu.world.LMPlayerServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdTplast extends CommandFTBU
{
	public CmdTplast()
	{ super("tpl", CommandLevel.OP); }
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0 || i == 1) ? Boolean.FALSE : null; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		
		if(args.length == 3)
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			double x = func_110666_a(ics, ep.posX, args[0]);
			double y = func_110666_a(ics, ep.posY, args[1]);
			double z = func_110666_a(ics, ep.posZ, args[2]);
			LMDimUtils.teleportPlayer(ep, x, y, z, ep.dimension);
			return null;
		}
		
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
		
		EntityPos p = to.getPos();
		if(p == null) return error(new ChatComponentText("No last position!"));
		LMDimUtils.teleportPlayer(who, p);
		return new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_tp", to.getName());
	}
}