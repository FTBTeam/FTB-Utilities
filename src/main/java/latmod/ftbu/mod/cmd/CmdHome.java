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
	{ return '/' + commandName + " <ID>\n/" + commandName + " set <ID>\n/" + commandName + " del <ID>\n/" + commandName + " ren <ID> <NewID>"; }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{
		if(i == 0 || (i == 1 && isArg(args, 0, "set", "del", "ren"))) return LMPlayerServer.get(ics).homes.list();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		if(args.length == 0)
		{
			FTBLib.printChat(ics, "/home <ID>");
			FTBLib.printChat(ics, "/home set <ID>");
			FTBLib.printChat(ics, "/home del <ID>");
			FTBLib.printChat(ics, "/home ren <ID> <NewID>");
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

		if(args[0].equals("ren"))
		{
			checkArgs(args, 3);
			EntityPos pos = p.homes.get(args[1]);
			if(pos == null) return error(new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.home_not_set", args[0]));

			pos = pos.clone();
			p.homes.rem(args[1]);
			p.homes.set(args[2], pos);
			return new ChatComponentText(args[1] + " => " + args[2]);
		}
		
		EntityPos pos = p.homes.get(args[0]);
		
		if(pos == null) return error(new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.home_not_set", args[0]));
		
		if(ep.dimension != pos.dim && !p.getRank().config.cross_dim_homes.get())
			return error(new ChatComponentTranslation(FTBU.mod.assets + "cmd.home_cross_dim"));
		
		LMDimUtils.teleportPlayer(ep, pos);
		return new ChatComponentTranslation(FTBUFinals.ASSETS + "cmd.warp_tp", args[0]);
	}
}