package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public interface IClaimedChunks
{
	@Nullable
	IClaimedChunk getChunk(ChunkDimPos pos);

	@Nullable
	default IForgePlayer getChunkOwner(ChunkDimPos pos)
	{
		IClaimedChunk c = getChunk(pos);
		return c == null ? null : c.getOwner();
	}

	Collection<? extends IClaimedChunk> getChunks(@Nullable IForgePlayer owner);

	boolean canPlayerInteract(EntityPlayerMP player, EnumHand hand, BlockPosContainer block, BlockInteractionType type);
}