package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBU;
import ftb.utils.world.FTBUWorldDataMP;
import latmod.lib.LMStringUtils;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

import java.util.*;

public class CmdWarp extends CommandLM
{
	public CmdWarp()
	{ super("warp", CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args, BlockPos pos)
	{
		if(args.length == 1) return getListOfStringsMatchingLastWord(args, FTBUWorldDataMP.get().warps.list());
		return null;
	}
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		if(args[0].equals("list"))
		{
			Collection<String> list = FTBUWorldDataMP.get().warps.list();
			ics.addChatMessage(new ChatComponentText(list.isEmpty() ? "-" : LMStringUtils.strip(list)));
			return;
		}
		
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		BlockDimPos p = FTBUWorldDataMP.get().warps.get(args[0]);
		if(p == null) throw new CommandException("ftbu.cmd.warp_not_set", args[0]);
		LMDimUtils.teleportPlayer(ep, p);
		ics.addChatMessage(FTBU.mod.chatComponent("cmd.warp_tp", args[0]));
	}
}