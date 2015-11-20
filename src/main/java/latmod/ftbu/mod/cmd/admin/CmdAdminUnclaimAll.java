package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.util.CommandFTBU;
import latmod.ftbu.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdAdminUnclaimAll extends CommandFTBU
{
	public CmdAdminUnclaimAll(String s)
	{ super(s, CommandLevel.OP); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		
		if(args[0].equals("@a"))
		{
			for(LMPlayer p : LMWorldServer.inst.players)
				p.toPlayerMP().claims.unclaimAll();
			return new ChatComponentText("Unclaimed all chunks");
		}
		
		LMPlayerServer p = getLMPlayer(args[0]);
		p.claims.unclaimAll();
		return new ChatComponentText("Unclaimed all " + p.getName() + "'s chunks");
	}
}