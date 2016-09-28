package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface IClaimedChunkStorage
{
    Map<ChunkDimPos, IForgePlayer> getAllChunks();

    IForgePlayer getChunkOwner(ChunkDimPos pos);

    void setOwner(ChunkDimPos pos, @Nullable IForgePlayer owner);

    Collection<ChunkDimPos> getChunks(@Nullable IForgePlayer player);

    boolean canPlayerInteract(EntityPlayerMP player, BlockPos pos, IMouseButton button);
}