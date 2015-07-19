package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.LMDimUtils;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdSpawn extends CommandLM
{
	public CmdSpawn()
	{ super("spawn", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		if(LMDimUtils.teleportPlayer(ep, LMDimUtils.getEntitySpawnPoint(0)))
			return FINE + "Teleported to spawn";
		return "Failed to teleport!";
	}
}