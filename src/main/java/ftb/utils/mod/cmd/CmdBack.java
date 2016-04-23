package ftb.utils.mod.cmd;

import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdBack extends CommandLM
{
	public CmdBack()
	{ super("back", CommandLevel.ALL); }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayerServer p = LMPlayerServer.get(ep);
		if(p.lastDeath == null) FTBULang.warp_no_dp.commandError();
		LMDimUtils.teleportEntity(ep, p.lastDeath);
		p.lastDeath = null;
	}
}