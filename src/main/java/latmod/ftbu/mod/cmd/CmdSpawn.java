package latmod.ftbu.mod.cmd;

import ftb.lib.*;
import latmod.ftbu.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class CmdSpawn extends CommandLM
{
	public CmdSpawn()
	{ super("spawn", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ChunkCoordinates spawnpoint = LMDimUtils.getSpawnPoint(0);
		
		World w = LMDimUtils.getWorld(0);
		
		while(w.getBlock(spawnpoint.posX, spawnpoint.posY, spawnpoint.posZ).isOpaqueCube())
			spawnpoint.posY++;
		
		LMDimUtils.teleportPlayer(ep, new EntityPos(spawnpoint, 0));
		return new ChatComponentText("Teleported to spawn"); //LANG
	}
}