package ftb.utils.cmd;

import ftb.lib.BlockDimPos;
import ftb.lib.LMDimUtils;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.FTBULang;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class CmdSpawn extends CommandLM
{
	public CmdSpawn()
	{ super("spawn", CommandLevel.ALL); }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		World w = LMDimUtils.getWorld(DimensionType.OVERWORLD);
		BlockPos spawnpoint = w.getSpawnPoint();
		
		while(w.getBlockState(spawnpoint).isFullCube()) spawnpoint = spawnpoint.up(2);
		
		LMDimUtils.teleportPlayer(ep, new BlockDimPos(spawnpoint, DimensionType.OVERWORLD));
		FTBULang.warp_spawn.printChat(ics);
	}
}