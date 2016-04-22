package ftb.utils.cmd;

import ftb.lib.api.*;
import ftb.lib.api.cmd.*;
import ftb.utils.*;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CmdSetHome extends CommandLM
{
	public CmdSetHome()
	{ super("sethome", CommandLevel.ALL); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender ics, String[] args, BlockPos pos)
	{
		if(args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUPlayerDataMP.get(ForgeWorldMP.inst.getPlayer(ics)).homes.list());
		}
		return null;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
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