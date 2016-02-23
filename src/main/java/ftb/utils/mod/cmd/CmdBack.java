package ftb.utils.mod.cmd;

import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.*;
import ftb.lib.api.players.LMPlayerMP;
import ftb.utils.mod.FTBU;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdBack extends CommandLM
{
	public CmdBack()
	{ super("back", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayerMP p = LMPlayerMP.get(ep);
		if(p.lastDeath == null) return error(new ChatComponentTranslation(FTBU.mod.assets + "cmd.no_dp"));
		LMDimUtils.teleportPlayer(ep, p.lastDeath);
		p.lastDeath = null;
		
		return null;
	}
}