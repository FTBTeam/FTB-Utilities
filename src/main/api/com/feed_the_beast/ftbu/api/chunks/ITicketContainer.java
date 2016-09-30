package com.feed_the_beast.ftbu.api.chunks;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.Map;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface ITicketContainer
{
    int getDimension();

    ForgeChunkManager.Ticket getTicket();

    Map<ChunkPos, ILoadedChunk> getChunks();

    void save();
}