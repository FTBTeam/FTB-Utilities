package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
import latmod.lib.LMStringUtils;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdHome extends CommandLM
{
	public CmdHome()
	{ super("home", CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{
		if(i == 0) return LMPlayerServer.get(ics).homes.list();
		return null;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayerServer p = LMPlayerServer.get(ep);
		checkArgs(args, 1);
		
		if(args[0].equals("list"))
		{
			String[] list = p.homes.list();
			ics.addChatMessage(new ChatComponentText(list.length + " / " + p.getRank().config.max_homes.getAsInt() + ": "));
			return (list.length == 0) ? null : new ChatComponentText(LMStringUtils.strip(list));
		}
		
		BlockDimPos pos = p.homes.get(args[0]);
		
		if(pos == null) return error(FTBULang.home_not_set.chatComponent(args[0]));
		
		if(ep.dimension != pos.dim && !p.getRank().config.cross_dim_homes.getAsBoolean())
			return error(FTBULang.home_cross_dim.chatComponent());
		
		LMDimUtils.teleportEntity(ep, pos);
		return FTBULang.warp_tp.chatComponent(args[0]);
	}
}