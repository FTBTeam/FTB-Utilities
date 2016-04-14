package ftb.utils.mod.cmd;

import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;

public class CmdBack extends CommandLM
{
	public CmdBack()
	{ super("back", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayerServer p = LMPlayerServer.get(ep);
		if(p.lastDeath == null) return error(FTBULang.warp_no_dp.chatComponent());
		LMDimUtils.teleportEntity(ep, p.lastDeath);
		p.lastDeath = null;
		
		return null;
	}
}