package ftb.utils.cmd.admin;

import ftb.lib.*;
import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.cmd.*;
import ftb.utils.FTBU;
import ftb.utils.world.FTBUPlayerDataMP;
import latmod.lib.LMStringUtils;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class CmdAdminHome extends CommandLM //FIXME: SubCommand
{
	public CmdAdminHome()
	{ super("home", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player> <sub> [ID]"; }
	
	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender ics, String[] args, BlockPos pos)
	{
		if(args.length == 2) return getListOfStringsMatchingLastWord(args, "list", "tp", "remove");
		return super.getTabCompletionOptions(server, ics, args, pos);
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 2);
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(ForgePlayerMP.get(args[0]));
		
		if(args[1].equals("list"))
		{
			ics.addChatMessage(new TextComponentString(LMStringUtils.strip(d.homes.list())));
			return;
		}
		
		checkArgs(args, 3);
		
		BlockDimPos pos = d.homes.get(args[2]);
		if(pos == null)
		{
			throw new CommandException("ftbu.cmd.home_not_set", args[2]);
		}
		
		if(args[1].equals("tp"))
		{
			LMDimUtils.teleportPlayer(getCommandSenderAsPlayer(ics), pos);
			ics.addChatMessage(FTBU.mod.chatComponent("cmd.warp_tp", args[2]));
			return;
		}
		else if(args[1].equals("remove"))
		{
			if(d.homes.set(args[2], null))
			{
				ics.addChatMessage(FTBU.mod.chatComponent("cmd.home_del", args[2]));
				return;
			}
		}
		
		throw new InvalidSubCommandException(args[2]);
	}
}