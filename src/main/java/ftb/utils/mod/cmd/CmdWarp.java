package ftb.utils.mod.cmd;

import ftb.lib.BlockDimPos;
import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMWorldServer;
import latmod.lib.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.List;
import java.util.Set;

public class CmdWarp extends CommandLM
{
	public CmdWarp()
	{ super("warp", CommandLevel.ALL); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		if(args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, LMWorldServer.inst.warps.list());
		}
		
		return super.addTabCompletionOptions(ics, args);
	}
	
	@Override
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