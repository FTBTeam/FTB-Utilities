package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.ForgePlayerMP;
import latmod.lib.LMStringUtils;
import net.minecraft.command.*;
import net.minecraft.util.ChatComponentText;

public class CmdListFriends extends CommandLM
{
	public CmdListFriends()
	{ super("list_friends", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player>"; }
	
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		ForgePlayerMP p = ForgePlayerMP.get(args[0]);
		ics.addChatMessage(new ChatComponentText(LMStringUtils.strip(p.getFriends())));
	}
}