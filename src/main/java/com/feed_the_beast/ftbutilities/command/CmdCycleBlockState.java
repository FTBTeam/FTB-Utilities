package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;

import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdCycleBlockState extends CmdBase
{
	public CmdCycleBlockState()
	{
		super("cycle_block_state", Level.OP);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		RayTraceResult result = MathUtils.rayTrace(player, false);

		if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			IBlockState state = player.world.getBlockState(result.getBlockPos());
			List<IBlockState> states = state.getBlock().getBlockState().getValidStates();
			player.world.setBlockState(result.getBlockPos(), states.get((states.indexOf(state) + 1) % states.size()), 3);

			TileEntity tileEntity = player.world.getTileEntity(result.getBlockPos());

			if (tileEntity != null)
			{
				tileEntity.updateContainingBlockInfo();
				tileEntity.markDirty();
			}
		}
	}
}