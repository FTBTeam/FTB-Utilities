package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.List;

public enum LoadedChunkStorage implements ForgeChunkManager.LoadingCallback
{
    INSTANCE;

    private static final TIntObjectHashMap<ForgeChunkManager.Ticket> TICKET_CONTAINERS = new TIntObjectHashMap<>();

    public void init()
    {
        if(!ForgeChunkManager.getConfig().hasCategory(FTBUFinals.MOD_ID))
        {
            ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumTicketCount", 100).setMinValue(0);
            ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumChunksPerTicket", 1000000).setMinValue(0);
            ForgeChunkManager.getConfig().save();
        }

        ForgeChunkManager.setForcedChunkLoadingCallback(FTBU.INST, this);
    }

    public void clear()
    {
        TICKET_CONTAINERS.clear();
    }

    @Nullable
    private ForgeChunkManager.Ticket request(int dimID, boolean createNew)
    {
        ForgeChunkManager.Ticket ticket = TICKET_CONTAINERS.get(dimID);

        if(ticket == null && createNew)
        {
            World world = DimensionManager.getWorld(dimID);

            if(world != null)
            {
                ticket = ForgeChunkManager.requestTicket(FTBU.INST, world, ForgeChunkManager.Type.NORMAL);
                TICKET_CONTAINERS.put(dimID, ticket);
            }
        }

        return ticket;
    }

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
    {
        int dim = world.provider.getDimension();
        TICKET_CONTAINERS.remove(dim);

        if(tickets.size() == 1)
        {
            TICKET_CONTAINERS.put(dim, tickets.get(0));
            checkDimension(world.provider.getDimension());
        }
    }

    public void checkAll()
    {
        for(IClaimedChunk chunk : ClaimedChunkStorage.INSTANCE.getChunks(null))
        {
            checkChunk(chunk, null);
        }
    }

    public void checkDimension(int dim)
    {
        ForgeChunkManager.Ticket ticket = request(dim, false);

        for(IClaimedChunk chunk : ClaimedChunkStorage.INSTANCE.getChunks(null))
        {
            if(chunk.getPos().dim == dim)
            {
                checkChunk(chunk, ticket);
            }
        }
    }

    public void checkChunk(IClaimedChunk chunk, @Nullable ForgeChunkManager.Ticket ticket)
    {
        boolean load = chunk.isActuallyLoaded();

        if(load != chunk.isForced())
        {
            chunk.setForced(load);

            if(ticket == null)
            {
                ticket = request(chunk.getPos().dim, load);
            }

            if(ticket != null)
            {
                if(load)
                {
                    ForgeChunkManager.forceChunk(ticket, chunk.getPos().getChunkPos());
                }
                else
                {
                    ForgeChunkManager.unforceChunk(ticket, chunk.getPos().getChunkPos());
                }
            }
        }
    }
}