package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.world.*;
import net.minecraft.command.*;
import net.minecraft.util.ChatComponentText;

public class CmdUnclaimAll extends CommandLM
{
	public CmdUnclaimAll()
	{ super("unclaim_all", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player | @a>"; }
	
	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args[0].equals("@a"))
		{
			for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
				p.unclaimAllChunks(null);
			ics.addChatMessage(new ChatComponentText("Unclaimed all chunks"));
		}
		else
		{
			LMPlayerServer p = LMPlayerServer.get(args[0]);
			p.unclaimAllChunks(null);
			ics.addChatMessage(new ChatComponentText("Unclaimed all " + p.getProfile().getName() + "'s chunks"));
		}
	}
}