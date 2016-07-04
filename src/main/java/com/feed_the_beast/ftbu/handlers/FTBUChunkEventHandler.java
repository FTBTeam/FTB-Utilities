package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.FTBLibStats;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.config.FTBUConfigModules;
import com.feed_the_beast.ftbu.world.ChunkloaderType;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import com.google.common.collect.MapMaker;
import com.latmod.lib.util.LMUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback, ForgeChunkManager.OrderedLoadingCallback
{
    public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
    private static final String PLAYER_ID_TAG = "PID";
    private final Map<World, Map<UUID, ForgeChunkManager.Ticket>> table = new MapMaker().weakKeys().makeMap();

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

    private ForgeChunkManager.Ticket request(World w, ForgePlayerMP player)
    {
        if(w == null || player == null)
        {
            return null;
        }

        UUID playerID = player.getProfile().getId();

        Map<UUID, ForgeChunkManager.Ticket> map = table.get(w);
        ForgeChunkManager.Ticket t = (map == null) ? null : map.get(playerID);

        if(t == null)
        {
            t = ForgeChunkManager.requestTicket(FTBU.inst, w, ForgeChunkManager.Type.NORMAL);
            if(t == null)
            {
                return null;
            }
            else
            {
                t.getModData().setString(PLAYER_ID_TAG, LMUtils.fromUUID(playerID));

                if(map == null)
                {
                    map = new HashMap<>();
                    table.put(w, map);
                }

                map.put(playerID, t);
            }
        }

        return t;
    }

    @Override
    public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount)
    {
        table.remove(world);
        List<ForgeChunkManager.Ticket> tickets1 = new ArrayList<>();
        if(tickets.isEmpty() || !FTBUConfigModules.chunk_loading.getAsBoolean())
        {
            return tickets1;
        }
        Map<UUID, ForgeChunkManager.Ticket> map = new HashMap<>();

        for(ForgeChunkManager.Ticket t : tickets)
        {
            if(t.getModData().getTagId(PLAYER_ID_TAG) == Constants.NBT.TAG_STRING)
            {
                UUID playerID = LMUtils.fromString(t.getModData().getString(PLAYER_ID_TAG));

                if(playerID != null)
                {
                    map.put(playerID, t);
                    tickets1.add(t);
                }
            }
        }

        table.put(world, map);
        return tickets1;
    }

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
    {
        for(ForgeChunkManager.Ticket t : tickets)
        {
            UUID playerID = LMUtils.fromString(t.getModData().getString(PLAYER_ID_TAG));

            if(playerID != null)
            {
                Collection<ClaimedChunk> chunks = FTBUWorldDataMP.chunks.getChunks(playerID);

                if(!chunks.isEmpty())
                {
                    int dim = world.provider.getDimension();

                    for(ClaimedChunk c : chunks)
                    {
                        if(c.loaded && c.pos.dim == dim)
                        {
                            ForgeChunkManager.forceChunk(t, c.pos);
                        }
                    }
                }
            }
        }

        // force chunks //
        markDirty(world);
    }

    public void markDirty(World w)
    {
        if(ForgeWorldMP.inst == null || FTBLib.getServerWorld() == null)
        {
            return;
        }
        if(w != null)
        {
            markDirty0(w);
        }

        if(!table.isEmpty())
        {
            World[] worlds = table.keySet().toArray(new World[table.size()]);
            for(World w1 : worlds)
            {
                markDirty0(w1);
            }
        }
    }

    private void markDirty0(World w)
    {
        /*int total = 0;
        int totalLoaded = 0;
		int markedLoaded = 0;
		int loaded = 0;
		int unloaded = 0;*/

        int dim = w.provider.getDimension();

        for(ClaimedChunk c : FTBUWorldDataMP.chunks.getAllChunks())
        {
            if(c.pos.dim == dim)
            {
                //total++;

                boolean isLoaded = c.loaded;

                if(isLoaded)
                {
                    ForgePlayerMP p = c.owner.toMP();

                    if(p == null)
                    {
                        isLoaded = false;
                    }
                    else
                    {
                        ChunkloaderType type = FTBUPermissions.CHUNKLOADER_TYPE.get(p.getProfile());

                        if(type == ChunkloaderType.DISABLED)
                        {
                            isLoaded = false;
                        }
                        else if(type == ChunkloaderType.ONLINE)
                        {
                            isLoaded = p.isOnline();
                        }
                        else if(type == ChunkloaderType.OFFLINE)
                        {
                            if(!p.isOnline())
                            {
                                double max = FTBUPermissions.CHUNKLOADER_OFFLINE_TIMER.get(p.getProfile());

                                if(max > 0D && FTBLibStats.getLastSeenDeltaInHours(p) > max)
                                {
                                    isLoaded = false;

                                    if(c.forced)
                                    {
                                        FTBU.logger.info("Unloading " + p.getProfile().getName() + " chunks for being offline for too long");
                                    }
                                }
                            }
                        }
                    }
                }

                //if(isLoaded) totalLoaded++;
                //if(c.isChunkloaded) markedLoaded++;

                if(c.forced != isLoaded)
                {
                    ForgeChunkManager.Ticket ticket = request(LMDimUtils.getWorld(c.pos.dim), c.owner.toMP());

                    if(ticket != null)
                    {
                        if(isLoaded)
                        {
                            ForgeChunkManager.forceChunk(ticket, c.pos);
                            //loaded++;
                        }
                        else
                        {
                            ForgeChunkManager.unforceChunk(ticket, c.pos);
                            //unloaded++;
                        }

                        c.forced = isLoaded;
                    }
                }
            }
        }

        //FTBLib.dev_logger.info("Total: " + total + ", Loaded: " + totalLoaded + "/" + markedLoaded + ", DLoaded: " + loaded + ", DUnloaded: " + unloaded);
    }

    /*
    private void releaseTicket(ForgeChunkManager.Ticket t)
    {
        if(t.getModData().hasKey(PLAYER_ID_TAG))
        {
            Map<UUID, ForgeChunkManager.Ticket> map = table.get(t.world);

            if(map != null)
            {
                map.remove(LMUtils.fromString(t.getModData().getString(PLAYER_ID_TAG)));

                if(map.isEmpty())
                {
                    table.remove(t.world);
                }
            }
        }

        ForgeChunkManager.releaseTicket(t);
    }
    */

    public void clear()
    {
        table.clear();
    }
}