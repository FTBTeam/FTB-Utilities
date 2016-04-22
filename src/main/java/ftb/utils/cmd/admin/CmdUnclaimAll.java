package ftb.utils.cmd.admin;

import ftb.lib.api.*;
import ftb.lib.api.cmd.*;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args[0].equals("@a"))
		{
			for(ForgePlayer p : ForgeWorldMP.inst.playerMap.values())
				FTBUPlayerDataMP.get(p.toPlayerMP()).unclaimAllChunks(null);
			ics.addChatMessage(new TextComponentString("Unclaimed all chunks"));
			return;
		}
		
		ForgePlayerMP p = ForgePlayerMP.get(args[0]);
		FTBUPlayerDataMP.get(p).unclaimAllChunks(null);
		ics.addChatMessage(new TextComponentString("Unclaimed all " + p.getProfile().getName() + "'s chunks"));
	}
}