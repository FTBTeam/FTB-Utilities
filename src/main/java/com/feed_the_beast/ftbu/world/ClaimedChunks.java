package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.handlers.FTBUChunkEventHandler;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.google.gson.JsonArray;
import com.mojang.authlib.GameProfile;
import latmod.lib.MathHelperLM;
import latmod.lib.util.EnumEnabled;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 21.05.2016.
 */
public class ClaimedChunks
{
    public static ClaimedChunks inst;

    private final Map<ChunkDimPos, ClaimedChunk> chunks;

    public ClaimedChunks()
    {
        chunks = new HashMap<>();
    }

    public static boolean isInSpawn(ChunkDimPos pos)
    {
        if(pos.dim != 0 || (!FTBLib.getServer().isDedicatedServer() && !FTBUConfigGeneral.spawn_area_in_sp.getAsBoolean()))
        {
            return false;
        }
        int radius = FTBLib.getServer().getSpawnProtectionSize();
        if(radius <= 0)
        {
            return false;
        }
        BlockPos c = FTBLib.getServer().getEntityWorld().getSpawnPoint();
        int minX = MathHelperLM.chunk(c.getX() + 0.5D - radius);
        int minZ = MathHelperLM.chunk(c.getZ() + 0.5D - radius);
        int maxX = MathHelperLM.chunk(c.getX() + 0.5D + radius);
        int maxZ = MathHelperLM.chunk(c.getZ() + 0.5D + radius);
        return pos.chunkXPos >= minX && pos.chunkXPos <= maxX && pos.chunkZPos >= minZ && pos.chunkZPos <= maxZ;
    }

    public static boolean isInSpawnD(int dim, double x, double z)
    {
        return dim == 0 && isInSpawn(new ChunkDimPos(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)));
    }

    public Collection<ClaimedChunk> getAllChunks(Integer dim)
    {
        if(dim == null)
        {
            return chunks.values();
        }
        else
        {
            Collection<ClaimedChunk> l = new HashSet<>();

            for(ClaimedChunk c : chunks.values())
            {
                if(c.pos.dim == dim.intValue())
                {
                    l.add(c);
                }
            }

            return l;
        }
    }

    public ClaimedChunk getChunk(ChunkDimPos pos)
    {
        return chunks.get(pos);
    }

    public ClaimedChunk putChunk(ClaimedChunk c)
    {
        return chunks.put(c.pos, c);
    }

    public ClaimedChunk getChunkD(int dim, BlockPos pos)
    {
        return getChunk(new ChunkDimPos(dim, MathHelperLM.chunk(pos.getX()), MathHelperLM.chunk(pos.getZ())));
    }

    public Collection<ClaimedChunk> getChunks(UUID playerID, Integer dim)
    {
        Collection<ClaimedChunk> list = new HashSet<>();
        if(playerID == null)
        {
            return list;
        }

        for(ClaimedChunk c : chunks.values())
        {
            if((dim == null || c.pos.dim == dim.intValue()) && c.owner.equals(playerID))
            {
                list.add(c);
            }
        }

        return list;
    }

    public boolean put(ClaimedChunk c)
    {
        return c != null && (chunks.put(c.pos, c) == null);
    }

    public ClaimedChunk remove(ChunkDimPos pos)
    {
        return chunks.remove(pos);
    }

    public boolean allowExplosion(ChunkDimPos pos)
    {
        if(pos.dim == 0 && FTBUConfigGeneral.safe_spawn.getAsBoolean() && isInSpawn(pos))
        {
            return false;
        }
        else
        {
            ClaimedChunk c = getChunk(pos);

            if(c != null)
            {
                EnumEnabled fe = FTBUPermissions.claims_forced_explosions.get(c.owner.getProfile());

                if(fe == null)
                {
                    return !c.owner.hasTeam() || FTBUTeamData.get(c.owner.getTeam()).toMP().explosions.getAsBoolean();
                }
                else
                {
                    return fe == EnumEnabled.ENABLED;
                }
            }
        }

        return true;
    }

    public int getClaimedChunks(UUID id)
    {
        return getChunks(id, null).size();
    }

    public int getLoadedChunks(UUID id, boolean forced)
    {
        int loaded = 0;
        for(ClaimedChunk c : getChunks(id, null))
        {
            if(c.loaded && (!forced || c.forced))
            {
                loaded++;
            }
        }

        return loaded;
    }

    public boolean isDimensionBlacklisted(GameProfile profile, int dim)
    {
        JsonArray a = FTBUPermissions.claims_dimension_blacklist.getJson(profile).getAsJsonArray();

        for(int i = 0; i < a.size(); i++)
        {
            if(a.get(i).getAsInt() == dim)
            {
                return true;
            }
        }

        return false;
    }

    public void claimChunk(ForgePlayer player, ChunkDimPos pos)
    {
        if(isDimensionBlacklisted(player.getProfile(), pos.dim))
        {
            return;
        }

        int max = FTBUPermissions.claims_max_chunks.get(player.getProfile());
        if(max == 0)
        {
            return;
        }

        if(getClaimedChunks(player.getProfile().getId()) >= max)
        {
            return;
        }

        ClaimedChunk chunk = getChunk(pos);

        if(chunk == null && put(new ClaimedChunk(player.getWorld(), player, pos)))
        {
            player.sendUpdate();
        }
    }

    public void unclaimChunk(ForgePlayer player, ChunkDimPos pos)
    {
        if(getChunk(pos).isChunkOwner(player))
        {
            setLoaded(player, pos, false);
            remove(pos);
            player.sendUpdate();
        }
    }

    public void unclaimAllChunks(ForgePlayer player, Integer dim)
    {
        Collection<ClaimedChunk> ch = getChunks(player.getProfile().getId(), dim);

        if(ch.isEmpty())
        {
            return;
        }

        for(ClaimedChunk c : ch)
        {
            setLoaded(player, c.pos, false);
            remove(c.pos);
        }

        player.sendUpdate();
    }

    public void setLoaded(ForgePlayer player, ChunkDimPos pos, boolean flag)
    {
        ClaimedChunk chunk = getChunk(pos);
        if(chunk == null)
        {
            return;
        }

        if(flag != chunk.loaded && player.equalsPlayer(chunk.owner))
        {
            if(flag)
            {
                if(isDimensionBlacklisted(player.getProfile(), pos.dim))
                {
                    return;
                }

                int max = FTBUPermissions.chunkloader_max_chunks.get(player.getProfile());
                if(max == 0)
                {
                    return;
                }

                if(getLoadedChunks(player.getProfile().getId(), false) >= max)
                {
                    return;
                }
            }

            chunk.loaded = flag;
            FTBUChunkEventHandler.instance.markDirty(LMDimUtils.getWorld(pos.dim));

            if(player.getPlayer() != null)
            {
                new MessageAreaUpdate(pos.chunkXPos, pos.chunkZPos, pos.dim, 1, 1).sendTo(player.toMP().getPlayer());
                player.sendUpdate();
            }
        }
    }
}