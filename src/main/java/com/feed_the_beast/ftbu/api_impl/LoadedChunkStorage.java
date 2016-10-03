package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.FTBLibStats;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
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

        ForgeChunkManager.setForcedChunkLoadingCallback(FTBU.inst, this);
    }

    public void clear()
    {
        TICKET_CONTAINERS.clear();
    }

    @Nullable
    private ForgeChunkManager.Ticket request(int dimID)
    {
        ForgeChunkManager.Ticket ticket = TICKET_CONTAINERS.get(dimID);

        if(ticket == null)
        {
            World world = DimensionManager.getWorld(dimID);

            if(world != null)
            {
                ticket = ForgeChunkManager.requestTicket(FTBU.inst, world, ForgeChunkManager.Type.NORMAL);
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

        if(tickets.size() != 1)
        {
            return;
        }

        ForgeChunkManager.Ticket ticket = tickets.get(0);
        TICKET_CONTAINERS.put(dim, ticket);
        check(ticket);
        
        /*
        if(FTBUConfigWorld.CHUNK_LOADING.getBoolean())
        {
            for(ChunkPos chunkPos : ticket.getChunkList())
            {
                IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(new ChunkDimPos(chunkPos, dim));

                if(chunk != null && chunk.isLoaded())
                {
                    ForgeChunkManager.forceChunk(ticket, chunkPos);
                    chunk.setForced(true);
                }
            }
        }*/
    }

    public void setLoaded(ChunkDimPos pos, boolean flag)
    {
        ForgeChunkManager.Ticket ticket = TICKET_CONTAINERS.get(pos.dim);

        if(!flag)
        {
            if(ticket != null)
            {
                ChunkPos chunkPos = pos.getChunkPos();
                IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);

                if(chunk != null)
                {
                    if(chunk.isForced())
                    {
                        ForgeChunkManager.unforceChunk(ticket, chunkPos);
                    }
                }
            }
        }
        else
        {
            if(ticket == null)
            {
                ticket = request(pos.dim);
            }

            if(ticket != null)
            {
                ForgeChunkManager.unforceChunk(ticket, pos.getChunkPos());
            }
        }
    }

    public void checkDimension(int dim)
    {
        ForgeChunkManager.Ticket ticket = TICKET_CONTAINERS.get(dim);

        if(ticket != null)
        {
            check(ticket);
        }
    }

    public void checkAll()
    {
        if(!TICKET_CONTAINERS.isEmpty())
        {
            for(ForgeChunkManager.Ticket ticket1 : new ArrayList<>(TICKET_CONTAINERS.valueCollection()))
            {
                check(ticket1);
            }
        }
    }

    public void check(ForgeChunkManager.Ticket ticket)
    {
        for(IClaimedChunk chunk : ClaimedChunkStorage.INSTANCE.getChunks(null))
        {
            if(chunk.getPos().dim != ticket.world.provider.getDimension())
            {
                continue;
            }

            boolean isForced = chunk.isForced();

            if(isForced)
            {
                IForgePlayer owner = FTBLibIntegration.API.getUniverse().getPlayer(chunk.getOwner());
                ChunkloaderType type = (ChunkloaderType) RankConfigAPI.getRankConfig(owner.getProfile(), FTBUPermissions.CHUNKLOADER_TYPE).getValue();

                if(type == ChunkloaderType.DISABLED)
                {
                    isForced = false;
                }
                else if(type == ChunkloaderType.ONLINE)
                {
                    if(!owner.isOnline())
                    {
                        isForced = false;
                    }
                }
                else if(type == ChunkloaderType.OFFLINE)
                {
                    if(!owner.isOnline())
                    {
                        double max = RankConfigAPI.getRankConfig(owner.getProfile(), FTBUPermissions.CHUNKLOADER_OFFLINE_TIMER).getDouble();

                        if(max > 0D && FTBLibStats.getLastSeenDeltaInHours(owner.stats(), false) > max)
                        {
                            isForced = false;

                            if(chunk.isForced())
                            {
                                FTBU.logger.info("Unloading " + owner.getProfile().getName() + " chunks for being offline for too long");
                            }
                        }
                    }
                }
            }

            if(isForced != chunk.isForced())
            {
                chunk.setForced(isForced);
                ForgeChunkManager.unforceChunk(ticket, chunk.getPos().getChunkPos());
            }
        }
    }

    public void checkChunk(IClaimedChunk chunk)
    {
    }
}