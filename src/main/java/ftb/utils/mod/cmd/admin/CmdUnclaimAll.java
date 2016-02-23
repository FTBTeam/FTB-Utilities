package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.*;
import ftb.utils.world.FTBUPlayerDataMP;
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
			for(LMPlayer p : LMWorldMP.inst.playerMap.values())
				FTBUPlayerDataMP.get(p.toPlayerMP()).unclaimAllChunks(null);
			return new ChatComponentText("Unclaimed all chunks");
		}
		
		LMPlayerMP p = LMPlayerMP.get(args[0]);
		FTBUPlayerDataMP.get(p).unclaimAllChunks(null);
		return new ChatComponentText("Unclaimed all " + p.getProfile().getName() + "'s chunks");
	}
}