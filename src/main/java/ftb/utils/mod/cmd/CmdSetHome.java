package ftb.utils.mod.cmd;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.LMPlayerMP;
import ftb.utils.mod.*;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.*;
import net.minecraft.util.IChatComponent;

public class CmdSetHome extends CommandLM
{
	public CmdSetHome()
	{ super("sethome", CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{
		if(i == 0) return FTBUPlayerDataMP.get(LMPlayerMP.get(ics)).homes.list();
		return null;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(LMPlayerMP.get(ics));
		checkArgs(args, 1);
		
		int maxHomes = FTBUPermissions.homes_max.get(d.player.getProfile()).getAsShort();
		
		if(maxHomes <= 0 || d.homes.size() >= maxHomes)
		{
			if(maxHomes == 0 || d.homes.get(args[0]) == null) return error(FTBU.mod.chatComponent("cmd.home_limit"));
		}
		
		d.homes.set(args[0], d.player.toPlayerMP().getPos());
		return FTBU.mod.chatComponent("cmd.home_set", args[0]);
	}
}