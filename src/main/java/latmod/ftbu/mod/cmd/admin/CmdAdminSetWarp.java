package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMWorldServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdAdminSetWarp extends SubCommand
{
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 1);
		EntityPlayerMP ep = CommandLM.getCommandSenderAsPlayer(ics);
		ChunkCoordinates c = ep.getPlayerCoordinates();
		LMWorldServer.inst.setWarp(args[0], c.posX, c.posY, c.posZ, ep.worldObj.provider.dimensionId);
		return new ChatComponentText("Warp '" + args[0] + "' set!"); //LANG
	}
}