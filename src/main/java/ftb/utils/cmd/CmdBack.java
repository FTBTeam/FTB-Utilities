package ftb.utils.cmd;

import ftb.lib.LMDimUtils;
import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.cmd.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CmdBack extends CommandLM
{
	public CmdBack()
	{ super("back", CommandLevel.ALL); }
	
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ForgePlayerMP p = ForgePlayerMP.get(ep);
		if(p.lastDeath == null) throw new CommandException("ftbu.cmd.no_dp");
		LMDimUtils.teleportPlayer(ep, p.lastDeath);
		p.lastDeath = null;
	}
}