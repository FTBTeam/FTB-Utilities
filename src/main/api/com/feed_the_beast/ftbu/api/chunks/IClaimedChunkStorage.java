package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface IClaimedChunkStorage
{
    @Nullable
    IClaimedChunk getChunk(ChunkDimPos pos);

    @Nullable
    default IForgePlayer getChunkOwner(ChunkDimPos pos)
    {
        IClaimedChunk c = getChunk(pos);
        return c == null ? null : c.getOwner();
    }

    void setChunk(ChunkDimPos pos, @Nullable IClaimedChunk chunk);

    Collection<IClaimedChunk> getChunks(@Nullable IForgePlayer owner);

    boolean canPlayerInteract(EntityPlayerMP player, EnumHand hand, BlockPos pos, @Nullable EnumFacing facing, BlockInteractionType type);
}