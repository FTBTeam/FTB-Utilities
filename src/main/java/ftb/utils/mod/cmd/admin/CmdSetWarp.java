package ftb.utils.mod.cmd.admin;

import ftb.lib.BlockDimPos;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMWorldServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdSetWarp extends CommandLM
{
	public CmdSetWarp()
	{ super("setwarp", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID> [x] [y] [z]"; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ChunkCoordinates c;
		
		if(args.length >= 4)
		{
			int x = parseInt(ics, args[1]);
			int y = parseInt(ics, args[2]);
			int z = parseInt(ics, args[3]);
			c = new ChunkCoordinates(x, y, z);
		}
		else c = ep.getPlayerCoordinates();
		
		LMWorldServer.inst.warps.set(args[0], new BlockDimPos(c, ep.dimension));
		return FTBULang.warp_set.chatComponent(args[0]);
	}
}