package latmod.ftbu.mod.cmd;

import ftb.lib.*;
import ftb.lib.cmd.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.config.FTBUConfigCmd;
import latmod.ftbu.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdHome extends CommandLM
{
	public CmdHome()
	{ super(FTBUConfigCmd.name_home.get(), CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>\n/" + commandName + " set <ID>\n/" + commandName + " del <ID>"; }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{
		if(i == 0 || (i == 1 && isArg(args, 0, "set", "del"))) return LMPlayerServer.get(ics).homes.list();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		if(args.length == 0)
		{
			FTBLib.printChat(ics, "/home <name>");
			FTBLib.printChat(ics, "/home set <name>");
			FTBLib.printChat(ics, "/home del <name>");
			return null;
		}
		
		LMPlayerServer p = LMPlayerServer.get(ep);
		
		if(args[0].equals("set"))
		{
			checkArgs(args, 2);
			
			int maxHomes = p.getRank().config.max_homes.get();
			if(maxHomes <= 0 || p.homes.size() >= maxHomes)
				return error(new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.home_limit"));
			
			p.homes.set(args[1], p.getPos());
			return new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.home_set", args[1]);
		}
		
		if(args[0].equals("del"))
		{
			checkArgs(args, 2);
			
			if(p.homes.rem(args[1]))
				return new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.home_del", args[1]);
			return error(new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.home_not_set", args[1]));
		}
		
		EntityPos pos = p.homes.get(args[0]);
		
		if(pos == null) return error(new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.home_not_set", args[0]));
		
		if(ep.dimension != pos.dim && !p.getRank().config.cross_dim_homes.get())
			return error(new ChatComponentTranslation(FTBU.mod.assets + "cmd.home_cross_dim"));
		
		LMDimUtils.teleportPlayer(ep, pos);
		return new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.warp_tp", args[0]);
	}
}