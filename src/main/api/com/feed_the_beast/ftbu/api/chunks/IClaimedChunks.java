package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * @author LatvianModder
 */
public interface IClaimedChunks
{
	void markDirty();

	@Nullable
	IClaimedChunk getChunk(ChunkDimPos pos);

	@Nullable
	default IForgeTeam getChunkTeam(ChunkDimPos pos)
	{
		IClaimedChunk chunk = getChunk(pos);
		return chunk == null ? null : chunk.getTeam();
	}

	Collection<? extends IClaimedChunk> getAllChunks();

	Collection<? extends IClaimedChunk> getAllChunksIgnoreConfig();

	Set<? extends IClaimedChunk> getTeamChunks(@Nullable IForgeTeam team, boolean includePending);

	default Set<? extends IClaimedChunk> getTeamChunks(@Nullable IForgeTeam team)
	{
		return getTeamChunks(team, false);
	}

	boolean canPlayerInteract(EntityPlayerMP player, EnumHand hand, BlockPosContainer block, BlockInteractionType type);

	boolean canPlayerAttackEntity(EntityPlayerMP player, Entity entity);
}