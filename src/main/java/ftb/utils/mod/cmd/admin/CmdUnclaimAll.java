package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.utils.world.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CmdUnclaimAll extends CommandLM
{
	public CmdUnclaimAll()
	{ super("unclaim_all", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player | @a>"; }
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0) ? Boolean.FALSE : null; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args[0].equals("@a"))
		{
			for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
				p.unclaimAllChunks(null);
			return new ChatComponentText("Unclaimed all chunks");
		}
		
		LMPlayerServer p = LMPlayerServer.get(args[0]);
		p.unclaimAllChunks(null);
		return new ChatComponentText("Unclaimed all " + p.getProfile().getName() + "'s chunks");
	}
}