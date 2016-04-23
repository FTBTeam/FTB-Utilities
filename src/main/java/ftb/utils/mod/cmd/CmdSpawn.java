package ftb.utils.mod.cmd;

import ftb.lib.BlockDimPos;
import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.mod.FTBULang;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class CmdSpawn extends CommandLM
{
	public CmdSpawn()
	{ super("spawn", CommandLevel.ALL); }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		World w = LMDimUtils.getWorld(0);
		ChunkCoordinates spawnpoint = w.getSpawnPoint();
		
		while(w.getBlock(spawnpoint.posX, spawnpoint.posY, spawnpoint.posZ).isOpaqueCube()) spawnpoint.posY += 2;
		
		LMDimUtils.teleportEntity(ep, new BlockDimPos(spawnpoint, 0));
		FTBULang.warp_spawn.printChat(ics);
	}
}