package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.cmd.*;
import latmod.ftbu.world.LMWorldServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdAdminSetWarp extends CommandLM
{
	public CmdAdminSetWarp(String s)
	{ super(s, CommandLevel.OP); }

	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ChunkCoordinates c = ep.getPlayerCoordinates();
		LMWorldServer.inst.setWarp(args[0], c.posX, c.posY, c.posZ, ep.worldObj.provider.dimensionId);
		return new ChatComponentText("Warp '" + args[0] + "' set!"); //LANG
	}
}