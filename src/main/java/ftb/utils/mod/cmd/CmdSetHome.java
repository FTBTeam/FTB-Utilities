package ftb.utils.mod.cmd;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.*;
import ftb.utils.mod.*;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.*;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CmdSetHome extends CommandLM
{
	public CmdSetHome()
	{ super("sethome", CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args, BlockPos pos)
	{
		if(args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUPlayerDataMP.get(ForgeWorldMP.inst.getPlayer(ics)).homes.list());
		}
		return null;
	}
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(ForgePlayerMP.get(ics));
		checkArgs(args, 1);
		
		int maxHomes = FTBUPermissions.homes_max.get(d.player.getProfile()).getAsShort();
		
		if(maxHomes <= 0 || d.homes.size() >= maxHomes)
		{
			if(maxHomes == 0 || d.homes.get(args[0]) == null)
			{
				throw new CommandException("ftbu.cmd.home_limit");
			}
		}
		
		d.homes.set(args[0], d.player.toPlayerMP().getPos());
		ics.addChatMessage(FTBU.mod.chatComponent("cmd.home_set", args[0]));
	}
}