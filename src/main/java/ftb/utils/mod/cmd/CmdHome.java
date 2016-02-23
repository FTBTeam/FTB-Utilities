package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.lib.api.players.LMPlayerMP;
import ftb.utils.mod.*;
import ftb.utils.world.FTBUPlayerDataMP;
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
		if(i == 0) return FTBUPlayerDataMP.get(LMPlayerMP.get(ics)).homes.list();
		return null;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(LMPlayerMP.get(ep));
		checkArgs(args, 1);
		
		if(args[0].equals("list"))
		{
			String[] list = d.homes.list();
			ics.addChatMessage(new ChatComponentText(list.length + " / " + FTBUPermissions.homes_max.getNumber(ep.getGameProfile()).shortValue() + ": "));
			return (list.length == 0) ? null : new ChatComponentText(LMStringUtils.strip(list));
		}
		
		BlockDimPos pos = d.homes.get(args[0]);
		
		if(pos == null) return error(FTBU.mod.chatComponent("cmd.home_not_set", args[0]));
		
		if(ep.dimension != pos.dim && !FTBUPermissions.homes_cross_dim.getBoolean(ep.getGameProfile()))
			return error(FTBU.mod.chatComponent("cmd.home_cross_dim"));
		
		LMDimUtils.teleportPlayer(ep, pos);
		return FTBU.mod.chatComponent("cmd.warp_tp", args[0]);
	}
}