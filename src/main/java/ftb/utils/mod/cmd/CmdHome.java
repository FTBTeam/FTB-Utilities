package ftb.utils.mod.cmd;

import ftb.lib.BlockDimPos;
import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
import latmod.lib.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.List;
import java.util.Set;

public class CmdHome extends CommandLM
{
	public CmdHome()
	{ super("home", CommandLevel.ALL); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		if(args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, LMPlayerServer.get(ics).homes.list());
		}
		
		return null;
	}
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayerServer p = LMPlayerServer.get(ep);
		checkArgs(args, 1);
		
		if(args[0].equals("list"))
		{
			Set<String> list = p.homes.list();
			ics.addChatMessage(new ChatComponentText(list.size() + " / " + p.getRank().config.max_homes.getAsInt() + ": "));
			ics.addChatMessage(new ChatComponentText(LMStringUtils.strip(list)));
		}
		
		BlockDimPos pos = p.homes.get(args[0]);
		
		if(pos == null) FTBULang.home_not_set.commandError(args[0]);
		
		if(ep.dimension != pos.dim && !p.getRank().config.cross_dim_homes.getAsBoolean())
			FTBULang.home_cross_dim.commandError();
		
		LMDimUtils.teleportEntity(ep, pos);
		FTBULang.warp_tp.printChat(ics, args[0]);
	}
}