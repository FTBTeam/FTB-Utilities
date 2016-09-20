package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunk;
import com.feed_the_beast.ftbu.api.chunks.ITicketContainer;
import com.latmod.lib.io.Bits;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public final class TicketContainer implements ITicketContainer
{
    private final int dimension;
    private final ForgeChunkManager.Ticket ticket;
    private final TLongObjectMap<ILoadedChunk> chunks;

    public TicketContainer(int dim, ForgeChunkManager.Ticket t)
    {
        dimension = dim;
        ticket = t;
        chunks = new TLongObjectHashMap<>();
    }

    @Override
    public int getDimension()
    {
        return dimension;
    }

    @Override
    public ForgeChunkManager.Ticket getTicket()
    {
        return ticket;
    }

    @Override
    public TLongObjectMap<ILoadedChunk> getChunks()
    {
        return chunks;
    }

    public void load()
    {
        chunks.clear();

        NBTTagList nbt = ticket.getModData().getTagList("Chunks", Constants.NBT.TAG_INT_ARRAY);

        for(int i = 0; i < nbt.tagCount(); i++)
        {
            int[] ai = nbt.getIntArrayAt(i);

            if(ai.length >= 7)
            {
                IForgePlayer player = FTBLibIntegration.API.getUniverse().getPlayer(new UUID(Bits.intsToLong(ai[2], ai[3]), Bits.intsToLong(ai[4], ai[5])));

                if(player != null)
                {
                    ILoadedChunk chunk = new LoadedChunk(new ChunkPos(ai[0], ai[1]), player);
                    chunk.setForced(ai[6] != 0);
                    chunks.put(Bits.intsToLong(ai[0], ai[1]), chunk);
                }
            }
        }
    }

    public void save()
    {
        NBTTagList nbt = new NBTTagList();

        for(ILoadedChunk chunk : chunks.valueCollection())
        {
            UUID uuid = chunk.getOwner().getProfile().getId();
            int[] ai = new int[7];
            ai[0] = chunk.getPos().chunkXPos;
            ai[1] = chunk.getPos().chunkZPos;
            ai[2] = Bits.intFromLongA(uuid.getMostSignificantBits());
            ai[3] = Bits.intFromLongB(uuid.getMostSignificantBits());
            ai[4] = Bits.intFromLongA(uuid.getLeastSignificantBits());
            ai[5] = Bits.intFromLongB(uuid.getLeastSignificantBits());
            ai[6] = chunk.isForced() ? 1 : 0;
            nbt.appendTag(new NBTTagIntArray(ai));
        }

        ticket.getModData().setTag("Chunks", nbt);
    }
}