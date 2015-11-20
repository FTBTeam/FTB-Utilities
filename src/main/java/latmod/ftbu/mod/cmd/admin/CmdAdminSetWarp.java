package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.CommandFTBU;
import latmod.ftbu.world.LMWorldServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdAdminSetWarp extends CommandFTBU
{
	public CmdAdminSetWarp(String s)
	{ super(s, CommandLevel.OP); }

	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ChunkCoordinates c = ep.getPlayerCoordinates();
		LMWorldServer.inst.warps.set(args[0], c.posX, c.posY, c.posZ, ep.worldObj.provider.dimensionId);
		return new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_set", args[0]);
	}
}