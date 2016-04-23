package ftb.utils.mod.cmd.admin;

import ftb.lib.BlockDimPos;
import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.CommandLevel;
import ftb.lib.api.cmd.CommandSubLM;
import ftb.lib.api.cmd.InvalidSubCommandException;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
import latmod.lib.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class CmdAdminHome extends CommandSubLM
{
	public CmdAdminHome()
	{ super("home", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID> [x] [y] [z]"; }
	
	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		if(args.length == 2) return getListOfStringsMatchingLastWord(args, "list", "tp", "remove");
		return super.addTabCompletionOptions(ics, args);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 2);
		LMPlayerServer p = LMPlayerServer.get(args[0]);
		
		if(args[1].equals("list")) return new ChatComponentText(LMStringUtils.strip(p.homes.list()));
		
		checkArgs(args, 3);
		
		BlockDimPos pos = p.homes.get(args[2]);
		if(pos == null) FTBULang.home_not_set.commandError(args[2]);
		
		if(args[1].equals("tp"))
		{
			LMDimUtils.teleportEntity(getCommandSenderAsPlayer(ics), pos);
			return FTBULang.warp_tp.chatComponent(args[2]);
		}
		else if(args[1].equals("remove"))
		{
			if(p.homes.set(args[2], null)) return FTBULang.home_del.chatComponent(args[2]);
		}
		
		throw new InvalidSubCommandException(args[2]);
	}
}