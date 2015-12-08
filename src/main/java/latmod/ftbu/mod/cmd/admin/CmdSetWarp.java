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
		ChunkCoordinates c;
		
		if(args.length >= 4)
		{
			c = new ChunkCoordinates();
			c.posX = parseInt(ics, args[1]);
			c.posY = parseInt(ics, args[2]);
			c.posZ = parseInt(ics, args[3]);
		}
		else c = ep.getPlayerCoordinates();
		
		LMWorldServer.inst.warps.set(args[0], c.posX, c.posY, c.posZ, ep.worldObj.provider.dimensionId);
		return new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_set", args[0]);
	}
}