package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMWorldServer;
import latmod.lib.LMStringUtils;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.*;

public class CmdWarp extends CommandLM
{
	public CmdWarp()
	{ super("warp", CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		if(args.length == 1) return getListOfStringsFromIterableMatchingLastWord(args, LMWorldServer.inst.warps.list());
		return super.addTabCompletionOptions(ics, args);
	}
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		if(args[0].equals("list"))
		{
			Set<String> list = LMWorldServer.inst.warps.list();
			ics.addChatMessage(new ChatComponentText(list.isEmpty() ? "-" : LMStringUtils.strip(list)));
			return;
		}
		
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		BlockDimPos p = LMWorldServer.inst.warps.get(args[0]);
		if(p == null) FTBULang.warp_not_set.commandError(args[0]);
		LMDimUtils.teleportEntity(ep, p);
		FTBULang.warp_tp.printChat(ics, args[0]);
	}
}