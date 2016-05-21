package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.handlers.FTBUChunkEventHandler;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import latmod.lib.LMJsonUtils;
import latmod.lib.LMUtils;
import latmod.lib.MathHelperLM;
import latmod.lib.util.EnumEnabled;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
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

    public final Map<ChunkDimPos, ClaimedChunk> chunks;

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

    public void save()
    {
        Map<UUID, JsonArray> map = new HashMap<>();

        for(Map.Entry<ChunkDimPos, ClaimedChunk> e : chunks.entrySet())
        {
            ForgePlayerMP p = e.getValue().getOwner();

            if(p != null)
            {
                ClaimedChunk c = e.getValue();

                JsonArray a = map.get(p.getProfile().getId());

                if(a == null)
                {
                    a = new JsonArray();
                    map.put(p.getProfile().getId(), a);
                }

                StringBuilder sb = new StringBuilder();

                sb.append(c.pos.dim);
                sb.append(',');

                sb.append(c.pos.chunkXPos);
                sb.append(',');

                sb.append(c.pos.chunkZPos);

                if(c.isChunkloaded)
                {
                    sb.append(',');
                    sb.append('L');
                }

                a.add(new JsonPrimitive(sb.toString()));
            }
        }

        JsonObject claimedChunksGroup = new JsonObject();

        for(Map.Entry<UUID, JsonArray> entry : map.entrySet())
        {
            claimedChunksGroup.add(LMUtils.fromUUID(entry.getKey()), entry.getValue());
        }

        LMJsonUtils.toJson(new File(ForgeWorldMP.inst.latmodFolder, "claimed_chunks.json"), claimedChunksGroup);
    }

    public void load()
    {
        chunks.clear();
        JsonElement claimsFile = LMJsonUtils.fromJson(new File(ForgeWorldMP.inst.latmodFolder, "claimed_chunks.json"));

        if(claimsFile.isJsonObject())
        {
            JsonObject claimedChunksGroup = claimsFile.getAsJsonObject();

            for(Map.Entry<String, JsonElement> e : claimedChunksGroup.entrySet())
            {
                try
                {
                    UUID id = LMUtils.fromString(e.getKey());

                    if(id != null)
                    {
                        if(ForgeWorldMP.inst.playerMap.containsKey(id))
                        {
                            for(JsonElement e1 : e.getValue().getAsJsonArray())
                            {
                                String[] s = e1.getAsString().split(",");

                                if(s.length >= 3)
                                {
                                    ClaimedChunk c = new ClaimedChunk(id, new ChunkDimPos(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));

                                    if(s.length > 3)
                                    {
                                        for(int i = 3; i < s.length; i++)
                                        {
                                            switch(s[i].charAt(0))
                                            {
                                                case 'L':
                                                {
                                                    c.isChunkloaded = true;
                                                    break;
                                                }
                                                //Possible other chunk properties
                                            }
                                        }
                                    }

                                    chunks.put(c.pos, c);
                                }
                            }
                        }
                    }
                    else
                    {
                        int dim = Integer.parseInt(e.getKey());

                        for(Map.Entry<String, JsonElement> e1 : e.getValue().getAsJsonObject().entrySet())
                        {
                            id = LMUtils.fromString(e1.getKey());

                            if(ForgeWorldMP.inst.playerMap.containsKey(id))
                            {
                                JsonArray chunksList = e1.getValue().getAsJsonArray();

                                for(int k = 0; k < chunksList.size(); k++)
                                {
                                    int[] ai = LMJsonUtils.fromIntArray(chunksList.get(k));

                                    if(ai != null)
                                    {
                                        ClaimedChunk c = new ClaimedChunk(id, new ChunkDimPos(dim, ai[0], ai[1]));
                                        if(ai.length >= 3 && ai[2] == 1)
                                        {
                                            c.isChunkloaded = true;
                                        }
                                        chunks.put(c.pos, c);
                                    }
                                }
                            }

                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
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

    public Collection<ClaimedChunk> getChunks(UUID playerID, Integer dim)
    {
        Collection<ClaimedChunk> list = new HashSet<>();
        if(playerID == null)
        {
            return list;
        }

        for(ClaimedChunk c : chunks.values())
        {
            if((dim == null || c.pos.dim == dim.intValue()) && c.ownerID.equals(playerID))
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

    public ChunkType getType(ForgePlayerMP p, ChunkDimPos pos)
    {
        World w = LMDimUtils.getWorld(pos.dim);
        if(w == null || w.getChunkProvider().getLoadedChunk(pos.chunkXPos, pos.chunkZPos) == null)
        {
            return ChunkType.UNLOADED;
        }
        if(isInSpawn(pos))
        {
            return ChunkType.SPAWN;
        }
        //TODO: if(ForgeWorldMP.inst.settings.getWB(dim).isOutside(cx, cz)) return ChunkType.WORLD_BORDER;
        ClaimedChunk c = getChunk(pos);
        if(c == null)
        {
            return ChunkType.WILDERNESS;
        }
        ChunkType.PlayerClaimed type = new ChunkType.PlayerClaimed();
        type.chunk = c;
        return type;
    }

    public ChunkType getTypeD(ForgePlayerMP p, int dim, BlockPos pos)
    {
        return getType(p, new ChunkDimPos(dim, MathHelperLM.chunk(pos.getX()), MathHelperLM.chunk(pos.getZ())));
    }

    public boolean allowExplosion(ChunkDimPos pos)
    {
        if(pos.dim == 0 && FTBUConfigGeneral.safe_spawn.getAsBoolean() && isInSpawn(pos))
        {
            return false;
        }
        //TODO: else if(ForgeWorldMP.inst.settings.getWB(dim).isOutside(cx, cz)) return false;
        else
        {
            ClaimedChunk c = getChunk(pos);
            if(c != null)
            {
                ForgePlayerMP p = c.getOwner();

                if(p != null)
                {
                    EnumEnabled fe = FTBUPermissions.claims_forced_explosions.getEnum(p.getProfile());
                    if(fe == null)
                    {
                        return FTBUPlayerData.get(p).getFlag(FTBUPlayerData.EXPLOSIONS);
                    }
                    else
                    {
                        return fe == EnumEnabled.ENABLED;
                    }
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
            if(c.isChunkloaded && (!forced || c.isForced))
            {
                loaded++;
            }
        }

        return loaded;
    }

    public short getMaxLoadedChunks(GameProfile profile)
    { return FTBUPermissions.chunkloader_max_chunks.get(profile).getAsShort(); }

    public boolean isDimensionBlacklisted(GameProfile profile, int dim)
    {
        JsonArray a = FTBUPermissions.claims_dimension_blacklist.get(profile).getAsJsonArray();

        for(int i = 0; i < a.size(); i++)
        {
            if(a.get(i).getAsInt() == dim) { return true; }
        }

        return false;
    }

    public void claimChunk(ForgePlayer player, ChunkDimPos pos)
    {
        if(isDimensionBlacklisted(player.getProfile(), pos.dim)) { return; }
        short max = FTBUPermissions.claims_max_chunks.get(player.getProfile()).getAsShort();
        if(max == 0) { return; }
        if(getClaimedChunks(player.getProfile().getId()) >= max) { return; }

        ChunkType t = getType(player.toPlayerMP(), pos);
        if(t.asClaimed() == null && t.isChunkOwner(player.toPlayerMP()) && put(new ClaimedChunk(player.getProfile().getId(), pos)))
        {
            player.sendUpdate();
        }
    }

    public void unclaimChunk(ForgePlayer player, ChunkDimPos pos)
    {
        if(getType(player.toPlayerMP(), pos).isChunkOwner(player.toPlayerMP()))
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

        if(flag != chunk.isChunkloaded && player.equalsPlayer(chunk.getOwner()))
        {
            if(flag)
            {
                if(isDimensionBlacklisted(player.getProfile(), pos.dim))
                {
                    return;
                }

                short max = getMaxLoadedChunks(player.getProfile());
                if(max == 0)
                {
                    return;
                }

                if(getLoadedChunks(player.getProfile().getId(), false) >= max)
                {
                    return;
                }
            }

            chunk.isChunkloaded = flag;
            FTBUChunkEventHandler.instance.markDirty(LMDimUtils.getWorld(pos.dim));

            if(player.getPlayer() != null)
            {
                new MessageAreaUpdate(player.toPlayerMP(), pos.chunkXPos, pos.chunkZPos, pos.dim, 1, 1).sendTo(player.toPlayerMP().getPlayer());
                player.sendUpdate();
            }
        }
    }
}