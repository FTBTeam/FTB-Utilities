package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBU;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CmdSpawn extends CommandLM
{
	public CmdSpawn()
	{ super("spawn", CommandLevel.ALL); }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		World w = LMDimUtils.getWorld(0);
		BlockPos spawnpoint = w.getSpawnPoint();
		
		while(w.getBlockState(spawnpoint).getBlock().isOpaqueCube()) spawnpoint = spawnpoint.up(2);
		
		LMDimUtils.teleportPlayer(ep, new BlockDimPos(spawnpoint, 0));
		ics.addChatMessage(FTBU.mod.chatComponent("cmd.spawn_tp"));
	}
}