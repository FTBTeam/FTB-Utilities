package latmod.ftbu.mod.cmd;

import ftb.lib.LMDimUtils;
import ftb.lib.cmd.CommandLevel;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.CommandFTBU;
import latmod.ftbu.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdBack extends CommandFTBU
{
	public CmdBack()
	{ super("back", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		if(p.lastDeath == null) return error(new ChatComponentTranslation(FTBU.mod.assets + "cmd.no_dp"));
		LMDimUtils.teleportPlayer(ep, p.lastDeath);
		p.lastDeath = null;
		
		return null;
	}
}