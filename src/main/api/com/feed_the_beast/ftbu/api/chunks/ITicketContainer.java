package com.feed_the_beast.ftbu.api.chunks;

import gnu.trove.map.TLongObjectMap;
import net.minecraftforge.common.ForgeChunkManager;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public interface ITicketContainer
{
    int getDimension();

    ForgeChunkManager.Ticket getTicket();

    TLongObjectMap<ILoadedChunk> getChunks();
}