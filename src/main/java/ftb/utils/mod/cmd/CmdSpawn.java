package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBULang;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class CmdSpawn extends CommandLM
{
	public CmdSpawn()
	{ super("spawn", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		World w = LMDimUtils.getWorld(0);
		ChunkCoordinates spawnpoint = w.getSpawnPoint();
		
		while(w.getBlock(spawnpoint.posX, spawnpoint.posY, spawnpoint.posZ).isOpaqueCube()) spawnpoint.posY += 2;
		
		LMDimUtils.teleportEntity(ep, new BlockDimPos(spawnpoint, 0));
		return FTBULang.warp_spawn.chatComponent();
	}
}