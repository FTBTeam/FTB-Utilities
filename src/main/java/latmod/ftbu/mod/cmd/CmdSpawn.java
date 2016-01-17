package latmod.ftbu.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigCmd;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class CmdSpawn extends CommandLM
{
	public CmdSpawn()
	{ super(FTBUConfigCmd.name_spawn.get(), CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		World w = LMDimUtils.getWorld(0);
		BlockPos spawnpoint = w.getSpawnPoint();
		
		while(w.getBlockState(spawnpoint).getBlock().isOpaqueCube()) spawnpoint = spawnpoint.up(2);
		
		LMDimUtils.teleportPlayer(ep, new EntityPos(spawnpoint, 0));
		return new ChatComponentTranslation(FTBU.mod.assets + "cmd.spawn_tp");
	}
}