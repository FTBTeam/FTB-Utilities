package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.*;
import latmod.ftbu.mod.config.FTBUConfigCmd;
import latmod.ftbu.world.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CmdUnclaimAll extends CommandLM
{
	public CmdUnclaimAll()
	{ super(FTBUConfigCmd.name_unclaim_all.get(), CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player | @a>"; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args[0].equals("@a"))
		{
			for(LMPlayer p : LMWorldServer.inst.players)
				p.toPlayerMP().claims.unclaimAll();
			return new ChatComponentText("Unclaimed all chunks");
		}
		
		LMPlayerServer p = LMPlayerServer.get(args[0]);
		p.claims.unclaimAll();
		return new ChatComponentText("Unclaimed all " + p.getName() + "'s chunks");
	}
}