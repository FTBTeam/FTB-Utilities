package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class CmdAdminSetWarp extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		CommandLM.checkArgs(args, 1);
		EntityPlayerMP ep = CommandLM.getCommandSenderAsPlayer(ics);
		ChunkCoordinates c = ep.getPlayerCoordinates();
		LMWorld.server.setWarp(args[0], c.posX, c.posY, c.posZ, ep.worldObj.provider.dimensionId);
		return CommandLM.FINE + "Warp '" + args[0] + "' set!";
	}
}