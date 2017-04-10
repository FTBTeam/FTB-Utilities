package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum LoadedChunkStorage implements ForgeChunkManager.LoadingCallback, ForgeChunkManager.OrderedLoadingCallback
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
    public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount)
    {
        return Collections.emptyList();
    }

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
    {
        int dim = world.provider.getDimension();
        FTBUFinals.LOGGER.info("Loaded chunks " + dim);
        ForgeChunkManager.Ticket ticket = TICKET_CONTAINERS.get(dim);

        if(ticket != null && ticket.world != null && ticket.getModId() != null)
        {
            ForgeChunkManager.releaseTicket(ticket);
        }
        else if(ticket != null)
        {
            FTBUFinals.LOGGER.warn("Damaged ticket found: " + ticket + ", world:" + ticket.world + ", modID:" + ticket.getModId());
        }

        TICKET_CONTAINERS.remove(dim);

        if(tickets.size() == 1)
        {
            TICKET_CONTAINERS.put(dim, tickets.get(0));
            checkDimension(world.provider.getDimension());
        }
        else if(tickets.size() > 1)
        {
            FTBUFinals.LOGGER.warn("There was an error while loading tickets! Releasing all [" + tickets.size() + "]!");
            new ArrayList<>(tickets).forEach(ForgeChunkManager::releaseTicket);
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
        boolean load = chunk.hasUpgrade(ChunkUpgrade.ACTUALLY_LOADED);

        if(load != chunk.hasUpgrade(ChunkUpgrade.FORCED))
        {
            chunk.setHasUpgrade(ChunkUpgrade.FORCED, load);

            if(ticket == null)
            {
                ticket = request(chunk.getPos().dim, load);
            }

            if(ticket != null)
            {
                ChunkPos pos = chunk.getPos().getChunkPos();

                if(load)
                {
                    if(!ticket.getChunkList().contains(pos))
                    {
                        ForgeChunkManager.forceChunk(ticket, pos);

                        if(FTBUConfigWorld.LOG_CHUNKLOADING.getBoolean())
                        {
                            FTBUFinals.LOGGER.info("Chunkloader forced " + chunk.getPos() + " by " + chunk.getOwner());
                        }
                    }
                }
                else
                {
                    if(ticket.getChunkList().contains(pos) && ticket.world != null)
                    {
                        ForgeChunkManager.unforceChunk(ticket, pos);

                        if(FTBUConfigWorld.LOG_CHUNKLOADING.getBoolean())
                        {
                            FTBUFinals.LOGGER.info("Chunkloader unforced " + chunk.getPos() + " by " + chunk.getOwner());
                        }
                    }
                }
            }
        }
    }
}