package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.LMWorldServer;
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
		BlockPos c;
		
		if(args.length >= 4)
		{
			int x = parseInt(args[1]);
			int y = parseInt(args[2]);
			int z = parseInt(args[3]);
			c = new BlockPos(x, y, z);
		}
		else c = ep.getPosition();
		
		LMWorldServer.inst.warps.set(args[0], c.getX(), c.getY(), c.getZ(), ep.dimension);
		return new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_set", args[0]);
	}
}