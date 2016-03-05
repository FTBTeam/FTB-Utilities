package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.*;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.*;
import net.minecraft.util.ChatComponentText;

public class CmdUnclaimAll extends CommandLM
{
	public CmdUnclaimAll()
	{ super("unclaim_all", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player | @a>"; }
	
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args[0].equals("@a"))
		{
			for(ForgePlayer p : ForgeWorldMP.inst.playerMap.values())
				FTBUPlayerDataMP.get(p.toPlayerMP()).unclaimAllChunks(null);
			ics.addChatMessage(new ChatComponentText("Unclaimed all chunks"));
			return;
		}
		
		ForgePlayerMP p = ForgePlayerMP.get(args[0]);
		FTBUPlayerDataMP.get(p).unclaimAllChunks(null);
		ics.addChatMessage(new ChatComponentText("Unclaimed all " + p.getProfile().getName() + "'s chunks"));
	}
}