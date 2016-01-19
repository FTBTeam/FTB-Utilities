package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.world.LMPlayerServer;
import latmod.lib.LMListUtils;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CmdListFriends extends CommandLM
{
	public CmdListFriends()
	{ super("list_friends", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player>"; }
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0) ? Boolean.TRUE : null; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		LMPlayerServer p = LMPlayerServer.get(args[0]);
		return new ChatComponentText(joinNiceString(LMListUtils.toStringArray(p.getFriends())));
	}
}