package ftb.utils.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.FTBU;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

public class CmdSpawn extends CommandLM
{
	public CmdSpawn()
	{ super("spawn", CommandLevel.ALL); }
	
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		World w = LMDimUtils.getWorld(DimensionType.OVERWORLD);
		BlockPos spawnpoint = w.getSpawnPoint();
		
		while(w.getBlockState(spawnpoint).isFullCube()) spawnpoint = spawnpoint.up(2);
		
		LMDimUtils.teleportPlayer(ep, new BlockDimPos(spawnpoint, DimensionType.OVERWORLD));
		ics.addChatMessage(FTBU.mod.chatComponent("cmd.spawn_tp"));
	}
}