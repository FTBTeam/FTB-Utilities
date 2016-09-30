package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.FTBLibStats;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunk;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunkStorage;
import com.feed_the_beast.ftbu.api.chunks.ITicketContainer;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.google.common.base.Objects;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum LoadedChunkStorage implements ForgeChunkManager.LoadingCallback, ILoadedChunkStorage
{
    INSTANCE;

    private final TIntObjectHashMap<TicketContainer> ticketContainers = new TIntObjectHashMap<>();

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
        ticketContainers.clear();
    }

    @Nullable
    private ITicketContainer request(int dimID)
    {
        TicketContainer ticketContainer = ticketContainers.get(dimID);

        if(ticketContainer == null)
        {
            World world = DimensionManager.getWorld(dimID);

            if(world != null)
            {
                ticketContainer = new TicketContainer(dimID, ForgeChunkManager.requestTicket(FTBU.inst, world, ForgeChunkManager.Type.NORMAL));
                ticketContainer.load();
                ticketContainers.put(dimID, ticketContainer);
            }
        }

        return ticketContainer;
    }

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
    {
        int dim = world.provider.getDimension();
        ticketContainers.remove(dim);

        if(tickets.size() != 1 || !FTBUConfigWorld.CHUNK_LOADING.getBoolean())
        {
            return;
        }

        ForgeChunkManager.Ticket ticket = tickets.get(0);
        TicketContainer ticketContainer = new TicketContainer(world.provider.getDimension(), ticket);
        ticketContainer.load();
        ticketContainers.put(dim, ticketContainer);

        for(ILoadedChunk loadedChunk : ticketContainer.getChunks().values())
        {
            if(Objects.equal(loadedChunk.getOwner(), FTBUtilitiesAPI_Impl.INSTANCE.getClaimedChunks().getChunkOwner(new ChunkDimPos(loadedChunk.getPos(), dim))))
            {
                ForgeChunkManager.forceChunk(ticket, loadedChunk.getPos());
            }
        }

        checkUnloaded(dim);
    }

    @Override
    @Nullable
    public ILoadedChunk getChunk(ChunkDimPos pos)
    {
        ITicketContainer ticketContainer = ticketContainers.get(pos.dim);

        if(ticketContainer != null)
        {
            return ticketContainer.getChunks().get(pos.getChunkPos());
        }

        return null;
    }

    @Override
    public boolean isLoaded(ChunkDimPos pos, @Nullable IForgePlayer player)
    {
        ILoadedChunk loadedChunk = getChunk(pos);
        return loadedChunk != null && (player == null || player.equals(loadedChunk.getOwner()));
    }

    @Override
    public void setLoaded(ChunkDimPos pos, @Nullable IForgePlayer player)
    {
        ITicketContainer ticketContainer = ticketContainers.get(pos.dim);

        if(player == null)
        {
            if(ticketContainer != null)
            {
                ChunkPos chunkPos = pos.getChunkPos();
                ILoadedChunk loadedChunk = ticketContainer.getChunks().get(chunkPos);

                if(loadedChunk != null)
                {
                    if(loadedChunk.isForced())
                    {
                        ForgeChunkManager.unforceChunk(ticketContainer.getTicket(), chunkPos);
                    }

                    ticketContainer.getChunks().remove(chunkPos);
                    ticketContainer.save();
                }
            }
        }
        else
        {
            if(ticketContainer == null)
            {
                ticketContainer = request(pos.dim);
            }

            if(ticketContainer != null)
            {
                ILoadedChunk loadedChunk = new LoadedChunk(pos.getChunkPos(), player);
                loadedChunk.setForced(true);
                ticketContainer.getChunks().put(loadedChunk.getPos(), loadedChunk);
            }
        }
    }

    @Override
    public Collection<ChunkDimPos> getChunks(@Nullable IForgePlayer player)
    {
        Collection<ChunkDimPos> c = new ArrayList<>();

        for(ITicketContainer ticketContainer : ticketContainers.valueCollection())
        {
            for(ILoadedChunk chunk : ticketContainer.getChunks().values())
            {
                if(player == null || (player.equals(chunk.getOwner())))
                {
                    c.add(new ChunkDimPos(chunk.getPos(), ticketContainer.getDimension()));
                }
            }
        }

        return c;
    }

    @Override
    public void checkUnloaded(Integer dimID)
    {
        if(dimID != null)
        {
            TicketContainer ticketContainer = ticketContainers.get(dimID);

            if(ticketContainer != null)
            {
                checkUnloaded0(ticketContainer);
            }
        }

        if(!ticketContainers.isEmpty())
        {
            for(TicketContainer ticketContainer : ticketContainers.values(new TicketContainer[ticketContainers.size()]))
            {
                checkUnloaded0(ticketContainer);
            }
        }
    }

    private void checkUnloaded0(ITicketContainer ticketContainer)
    {
        for(ILoadedChunk loadedChunk : new ArrayList<>(ticketContainer.getChunks().values()))
        {
            boolean isForced = loadedChunk.isForced();

            if(isForced)
            {
                IForgePlayer owner = FTBLibIntegration.API.getUniverse().getPlayer(loadedChunk.getOwner());
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

                            if(loadedChunk.isForced())
                            {
                                FTBU.logger.info("Unloading " + owner.getProfile().getName() + " chunks for being offline for too long");
                            }
                        }
                    }
                }
            }

            if(!isForced && loadedChunk.isForced())
            {
                loadedChunk.setForced(false);
                ForgeChunkManager.unforceChunk(ticketContainer.getTicket(), loadedChunk.getPos());
            }
        }

        ticketContainer.save();

        if(ticketContainer.getChunks().isEmpty())
        {
            ForgeChunkManager.releaseTicket(ticketContainer.getTicket());
            ticketContainers.remove(ticketContainer.getDimension());
        }
    }

    @Override
    public int getLoadedChunks(@Nullable IForgePlayer player)
    {
        Collection<ChunkDimPos> c = FTBUtilitiesAPI_Impl.INSTANCE.getClaimedChunks().getChunks(player);

        if(c.isEmpty())
        {
            return 0;
        }

        int loaded = 0;

        for(ChunkDimPos chunk : c)
        {
            if(isLoaded(chunk, player))
            {
                loaded++;
            }
        }

        return loaded;
    }
}