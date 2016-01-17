package latmod.ftbu.mod.cmd;

import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigCmd;
import latmod.ftbu.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdBack extends CommandLM
{
	public CmdBack()
	{ super(FTBUConfigCmd.name_back.get(), CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayerServer p = LMPlayerServer.get(ep);
		if(p.lastDeath == null) return error(new ChatComponentTranslation(FTBU.mod.assets + "cmd.no_dp"));
		LMDimUtils.teleportPlayer(ep, p.lastDeath);
		p.lastDeath = null;
		
		return null;
	}
}