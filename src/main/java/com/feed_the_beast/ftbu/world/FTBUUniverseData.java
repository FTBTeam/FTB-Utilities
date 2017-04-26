package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.BroadcastSender;
import com.feed_the_beast.ftbl.lib.internal.FTBLibStats;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.ChunkModifiedEvent;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.cmd.CmdRestart;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.world.backups.Backups;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by LatvianModder on 18.05.2016.
 */
public class FTBUUniverseData implements INBTSerializable<NBTBase>, ITickable
{
    private static final String FAILED_BADGE = FTBUFinals.MOD_ID + ":textures/failed_badge.png";
    private static final String BADGE_BASE_URL = "http://api.latmod.com/badges/get?id=";

    private static final Map<UUID, String> BADGE_CACHE = new HashMap<>();
    private static final Map<UUID, String> LOCAL_BADGES = new HashMap<>();
    public static final Function<ChunkDimPos, Boolean> ALLOW_EXPLOSION = pos ->
    {
        if(pos.dim == 0 && FTBUConfigWorld.SAFE_SPAWN.getBoolean() && isInSpawn(pos))
        {
            return false;
        }
        else
        {
            IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);
            return chunk == null || !chunk.hasUpgrade(ChunkUpgrade.NO_EXPLOSIONS);
        }
    };

    @Nullable
    public static FTBUUniverseData get()
    {
        return (FTBUUniverseData) FTBLibIntegration.API.getUniverse().getData(FTBLibIntegration.FTBU_DATA);
    }

    public long restartMillis;
    private long nextChunkloaderUpdate, nextWebApiUpdate;
    private final Map<String, BlockDimPos> warps = new HashMap<>();
    private String lastRestartMessage = "";

    public static void updateBadge(UUID playerId)
    {
        BADGE_CACHE.remove(playerId);
    }

    public static String getBadge(UUID playerId)
    {
        String b = BADGE_CACHE.get(playerId);

        if(b != null)
        {
            return b;
        }

        b = getRawBadge(playerId);
        BADGE_CACHE.put(playerId, b);
        return b;
    }

    private static String getRawBadge(UUID playerId)
    {
        IForgePlayer player = FTBLibIntegration.API.getUniverse().getPlayer(playerId);

        if(player == null || player.isFake())
        {
            return "";
        }

        FTBUPlayerData data = FTBUPlayerData.get(player);

        if(data == null || !data.renderBadge.getBoolean())
        {
            return "";
        }
        else if(!data.disableGlobalBadge.getBoolean())
        {
            try
            {
                return StringUtils.readString(new URL(BADGE_BASE_URL + StringUtils.fromUUID(playerId)).openStream());
            }
            catch(Exception ex)
            {
                return FAILED_BADGE;
            }
        }

        return FTBUtilitiesAPI_Impl.INSTANCE.getRankConfig(player.getProfile(), FTBUPermissions.BADGE).getString();
    }

    public static void reloadServerBadges()
    {
        try
        {
            BADGE_CACHE.clear();
            LOCAL_BADGES.clear();
            File file = new File(LMUtils.folderLocal, "ftbu/server_badges.json");

            if(!file.exists())
            {
                JsonObject o = new JsonObject();
                o.add("uuid", new JsonPrimitive("url_to.png"));
                JsonUtils.toJson(file, o);
            }
            else
            {
                for(Map.Entry<String, JsonElement> entry : JsonUtils.fromJson(file).getAsJsonObject().entrySet())
                {
                    UUID id = StringUtils.fromString(entry.getKey());

                    if(id != null)
                    {
                        LOCAL_BADGES.put(id, entry.getValue().getAsString());
                    }
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static boolean isInSpawn(ChunkDimPos pos)
    {
        MinecraftServer server = ServerUtils.getServer();

        if(pos.dim != 0 || (!server.isDedicatedServer() && !FTBUConfigWorld.SPAWN_AREA_IN_SP.getBoolean()))
        {
            return false;
        }

        int radius = server.getSpawnProtectionSize();
        if(radius <= 0)
        {
            return false;
        }

        BlockPos c = server.getEntityWorld().getSpawnPoint();
        int minX = MathUtils.chunk(c.getX() - radius);
        int minZ = MathUtils.chunk(c.getZ() - radius);
        int maxX = MathUtils.chunk(c.getX() + radius);
        int maxZ = MathUtils.chunk(c.getZ() + radius);
        return pos.posX >= minX && pos.posX <= maxX && pos.posZ >= minZ && pos.posZ <= maxZ;
    }

    public static boolean isInSpawnD(int dim, double x, double z)
    {
        return dim == 0 && isInSpawn(new ChunkDimPos(MathUtils.chunk(x), MathUtils.chunk(z), dim));
    }

    public static void handleExplosion(World world, Explosion explosion)
    {
        if(world.isRemote || explosion.getAffectedBlockPositions().isEmpty())
        {
            return;
        }

        List<BlockPos> list = new ArrayList<>(explosion.getAffectedBlockPositions());
        explosion.clearAffectedBlockPositions();
        Map<ChunkDimPos, Boolean> map = new HashMap<>();

        for(BlockPos pos : list)
        {
            if(map.computeIfAbsent(new ChunkDimPos(pos, world.provider.getDimension()), ALLOW_EXPLOSION))
            {
                explosion.getAffectedBlockPositions().add(pos);
            }
        }
    }

    public static boolean claimChunk(IForgePlayer player, ChunkDimPos pos)
    {
        if(!FTBUConfigWorld.CHUNK_CLAIMING.getBoolean() || !FTBUPermissions.allowDimension(player.getProfile(), pos.dim))
        {
            return false;
        }

        if(player.getTeam() == null)
        {
            if(player.isOnline())
            {
                FTBLibIntegration.API.sendNotification(player.getPlayer(), FTBUNotifications.NO_TEAM);
            }

            return false;
        }

        int max = FTBUtilitiesAPI_Impl.INSTANCE.getRankConfig(player.getProfile(), FTBUPermissions.CLAIMS_MAX_CHUNKS).getInt();
        if(max == 0)
        {
            return false;
        }

        if(ClaimedChunkStorage.INSTANCE.getChunks(player).size() >= max)
        {
            return false;
        }

        IForgePlayer chunkOwner = ClaimedChunkStorage.INSTANCE.getChunkOwner(pos);

        if(chunkOwner != null)
        {
            return false;
        }

        ClaimedChunk chunk = new ClaimedChunk(pos, player, 0);
        ClaimedChunkStorage.INSTANCE.setChunk(pos, chunk);
        MinecraftForge.EVENT_BUS.post(new ChunkModifiedEvent.Claimed(chunk));
        return true;
    }

    public static boolean unclaimChunk(IForgePlayer player, ChunkDimPos pos)
    {
        IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);

        if(chunk != null)
        {
            setLoaded(player, pos, false);
            MinecraftForge.EVENT_BUS.post(new ChunkModifiedEvent.Unclaimed(chunk));
            ClaimedChunkStorage.INSTANCE.setChunk(pos, null);
            return true;
        }

        return false;
    }

    public static void unclaimAllChunks(IForgePlayer player, @Nullable Integer dim)
    {
        for(IClaimedChunk chunk : ClaimedChunkStorage.INSTANCE.getChunks(player))
        {
            ChunkDimPos pos = chunk.getPos();
            if(dim == null || dim.intValue() == pos.dim)
            {
                setLoaded(player, pos, false);
                MinecraftForge.EVENT_BUS.post(new ChunkModifiedEvent.Unclaimed(chunk));
                ClaimedChunkStorage.INSTANCE.setChunk(pos, null);
            }
        }
    }

    public static boolean setLoaded(IForgePlayer player, ChunkDimPos pos, boolean flag)
    {
        IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);

        if(chunk == null || flag == chunk.hasUpgrade(ChunkUpgrade.LOADED) || !player.equalsPlayer(chunk.getOwner()))
        {
            return false;
        }

        if(flag)
        {
            if(player.getTeam() == null)
            {
                if(player.isOnline())
                {
                    FTBLibIntegration.API.sendNotification(player.getPlayer(), FTBUNotifications.NO_TEAM);
                }

                return false;
            }

            if(!FTBUPermissions.allowDimension(player.getProfile(), pos.dim))
            {
                return false;
            }

            int max = FTBUtilitiesAPI_Impl.INSTANCE.getRankConfig(player.getProfile(), FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();

            if(max == 0)
            {
                return false;
            }

            int loadedChunks = 0;

            for(IClaimedChunk c : ClaimedChunkStorage.INSTANCE.getChunks(player))
            {
                if(c.hasUpgrade(ChunkUpgrade.LOADED))
                {
                    loadedChunks++;

                    if(loadedChunks >= max)
                    {
                        return false;
                    }
                }
            }
        }
        else
        {
            MinecraftForge.EVENT_BUS.post(new ChunkModifiedEvent.Unloaded(chunk));
        }

        chunk.setHasUpgrade(ChunkUpgrade.LOADED, flag);
        LoadedChunkStorage.INSTANCE.checkChunk(chunk, null);

        if(flag)
        {
            MinecraftForge.EVENT_BUS.post(new ChunkModifiedEvent.Loaded(chunk));
        }

        return true;
    }

    public void onLoaded()
    {
        ClaimedChunkStorage.INSTANCE.init();

        long startMillis = System.currentTimeMillis();
        Backups.INSTANCE.nextBackup = startMillis + FTBUConfigBackups.backupMillis();
        lastRestartMessage = "";

        if(FTBUConfigGeneral.AUTO_RESTART.getBoolean() && FTBUConfigGeneral.RESTART_TIMER.getInt() > 0)
        {
            restartMillis = startMillis + (long) (FTBUConfigGeneral.RESTART_TIMER.getInt() * 3600D * 1000D);
            FTBUFinals.LOGGER.info("Server restart in " + StringUtils.getTimeString(restartMillis));
        }

        FTBLibIntegration.API.ticking().add(this);

        LOCAL_BADGES.clear();
    }

    public void onLoadedBeforePlayers()
    {
        ClaimedChunkStorage.INSTANCE.clear();
    }

    public void onClosed()
    {
        ClaimedChunkStorage.INSTANCE.clear();
        LoadedChunkStorage.INSTANCE.clear();
        LOCAL_BADGES.clear();
    }

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        if(!warps.isEmpty())
        {
            NBTTagCompound tag1 = new NBTTagCompound();

            for(Map.Entry<String, BlockDimPos> e : warps.entrySet())
            {
                tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
            }

            nbt.setTag("Warps", tag1);
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTBase nbt0)
    {
        nextChunkloaderUpdate = System.currentTimeMillis() + 10000L;
        warps.clear();
        nextWebApiUpdate = 0L;

        if(!(nbt0 instanceof NBTTagCompound))
        {
            return;
        }

        NBTTagCompound nbt = (NBTTagCompound) nbt0;

        if(nbt.hasKey("Warps"))
        {
            NBTTagCompound nbt1 = (NBTTagCompound) nbt.getTag("Warps");

            if(nbt1 != null && !nbt1.hasNoTags())
            {
                for(String s1 : nbt1.getKeySet())
                {
                    setWarp(s1.toLowerCase(), new BlockDimPos(nbt1.getIntArray(s1)));
                }
            }
        }

        if(nbt.hasKey("Chunks", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound nbt1 = nbt.getCompoundTag("Chunks");

            for(String s : nbt1.getKeySet())
            {
                IForgePlayer player = FTBLibIntegration.API.getUniverse().getPlayer(StringUtils.fromString(s));

                if(player != null)
                {
                    NBTTagList list = nbt1.getTagList(s, Constants.NBT.TAG_INT_ARRAY);

                    for(int i = 0; i < list.tagCount(); i++)
                    {
                        int[] ai = list.getIntArrayAt(i);

                        if(ai.length >= 3)
                        {
                            ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(ai[1], ai[2], ai[0]), player, ai.length >= 4 ? ai[3] : 0);
                            ClaimedChunkStorage.INSTANCE.setChunk(chunk.getPos(), chunk);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update()
    {
        long now = System.currentTimeMillis();

        if(restartMillis > 0L)
        {
            int secondsLeft = (int) ((restartMillis - System.currentTimeMillis()) / 1000L);
            String msg = StringUtils.getTimeString(secondsLeft * 1000L);

            if(!lastRestartMessage.equals(msg))
            {
                lastRestartMessage = msg;

                if(secondsLeft <= 0)
                {
                    CmdRestart.restart();
                    return;
                }
                else if(secondsLeft <= 10 || secondsLeft == 60 || secondsLeft == 300 || secondsLeft == 600 || secondsLeft == 1800)
                {
                    ITextComponent c = FTBULang.TIMER_RESTART.textComponent(msg);
                    c.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
                    BroadcastSender.INSTANCE.sendMessage(c);
                }
            }
        }

        if(Backups.INSTANCE.nextBackup > 0L && Backups.INSTANCE.nextBackup <= now)
        {
            MinecraftServer server = ServerUtils.getServer();
            Backups.INSTANCE.run(server, server, "");
        }

        if(nextChunkloaderUpdate < now)
        {
            nextChunkloaderUpdate = now + 2L * 3600L;
            LoadedChunkStorage.INSTANCE.checkAll();
        }

        if(Backups.INSTANCE.thread != null && Backups.INSTANCE.thread.isDone)
        {
            Backups.INSTANCE.thread = null;
            Backups.INSTANCE.postBackup();
        }

        if(FTBUConfigWebAPI.ENABLED.getBoolean() && nextWebApiUpdate < now)
        {
            nextWebApiUpdate = now + (FTBUConfigWebAPI.UPDATE_INTERVAL.getInt() * 60000L);
            exportWebAPI();
        }

        /*
        for(int i = teleportRequests.size() - 1; i >= 0; i--)
        {
            if(teleportRequests.get(i).isExpired(now))
            {
                teleportRequests.remove(i);
            }
        }
        */
    }

    public static void exportWebAPI()
    {
        try
        {
            JsonTable table = new JsonTable();
            table.setTitle("name", "Name");
            table.setTitle("deaths", "Deaths");
            table.setTitle("dph", "Deaths per hour");
            table.setTitle("last_seen", "Last time seen");

            for(IForgePlayer player : FTBLibIntegration.API.getUniverse().getPlayers())
            {
                StatisticsManagerServer stats = player.stats();

                JsonTable.TableEntry tableEntry = new JsonTable.TableEntry();
                tableEntry.set("name", new JsonPrimitive(player.getName()));
                tableEntry.set("deaths", new JsonPrimitive(stats.readStat(StatList.DEATHS)));
                tableEntry.set("dph", new JsonPrimitive(FTBLibStats.getDeathsPerHour(stats)));
                tableEntry.set("last_seen", new JsonPrimitive(player.isOnline() ? 0 : FTBLibStats.getLastSeen(stats, false)));
                table.addEntry(tableEntry);
            }

            JsonObject json = new JsonObject();
            json.add("time", new JsonPrimitive(System.currentTimeMillis()));
            json.add("stats", table.toJson());

            File file = FTBUConfigWebAPI.FILE_LOCATION.getString().isEmpty() ? new File(LMUtils.folderLocal, "ftbu/webapi.json") : new File(FTBUConfigWebAPI.FILE_LOCATION.getString());
            JsonUtils.toJson(JsonUtils.GSON, file, json);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public Collection<String> listWarps()
    {
        if(warps.isEmpty())
        {
            return Collections.emptySet();
        }

        return warps.keySet();
    }

    @Nullable
    public BlockDimPos getWarp(String s)
    {
        return warps.get(s);
    }

    public boolean setWarp(String s, @Nullable BlockDimPos pos)
    {
        if(pos == null)
        {
            return warps.remove(s) != null;
        }

        return warps.put(s, pos.copy()) == null;
    }

    public int warpsSize()
    {
        return warps.size();
    }
}
