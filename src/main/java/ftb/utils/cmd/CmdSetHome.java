package ftb.utils.cmd;

import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.ForgeWorldMP;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.FTBULang;
import ftb.utils.FTBUPermissions;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
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
		FTBULang.home_set.printChat(ics, args[0]);
	}
}