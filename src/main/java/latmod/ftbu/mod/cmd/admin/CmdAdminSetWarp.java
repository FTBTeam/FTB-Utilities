package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.SubCommand;
import net.minecraft.command.ICommandSender;

public class CmdAdminSetWarp extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		/*
		 * checkArgs(args, 1);
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			ChunkCoordinates c = ep.getPlayerCoordinates();
			
			EnkiData.Warps.setWarp(args[0], c.posX, c.posY, c.posZ, ep.worldObj.provider.dimensionId);
			return FINE + "Warp '" + args[0] + "' set!";
		 */
		return "Unimplemented!";
	}
}