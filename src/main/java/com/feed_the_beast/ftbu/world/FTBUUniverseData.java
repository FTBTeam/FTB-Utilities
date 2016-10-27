package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.BroadcastSender;
import com.feed_the_beast.ftbl.lib.EnumEnabled;
import com.feed_the_beast.ftbl.lib.INBTData;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.ChunkModifiedEvent;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.badges.BadgeStorage;
import com.feed_the_beast.ftbu.cmd.CmdRestart;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.webapi.WebAPI;
import com.feed_the_beast.ftbu.world.backups.Backups;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 18.05.2016.
 */
public class FTBUUniverseData implements INBTData, ITickable
{
    private static final ResourceLocation ID = new ResourceLocation(FTBUFinals.MOD_ID, "data");
    public static final BadgeStorage LOCAL_BADGES = new BadgeStorage();

    @Nullable
    public static FTBUUniverseData get()
    {
        return (FTBUUniverseData) FTBLibIntegration.API.getUniverse().getData(ID);
    }

    public long restartMillis;
    private long nextChunkloaderUpdate;
    private Map<String, BlockDimPos> warps;
    private String lastRestartMessage;

    @Override
    public ResourceLocation getID()
    {
        return ID;
    }

    @Nullable
    public static String getServerBadge(@Nullable IForgePlayer p)
    {
        if(p == null)
        {
            return null;
        }

        String b = LOCAL_BADGES.map.get(p.getProfile().getId());
        if(b != null)
        {
            return b;
        }

        b = RankConfigAPI.getRankConfig(p.getProfile(), FTBUPermissions.DISPLAY_BADGE).getString();

        if(!b.isEmpty())
        {
            return b;
        }

        return null;
    }

    public static void reloadServerBadges()
    {
        try
        {
            LOCAL_BADGES.clear();
            LOCAL_BADGES.loadBadges(LMJsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/badges.json")));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static boolean isInSpawn(ChunkDimPos pos)
    {
        MinecraftServer server = LMServerUtils.getServer();

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
        int minX = MathHelperLM.chunk(c.getX() - radius);
        int minZ = MathHelperLM.chunk(c.getZ() - radius);
        int maxX = MathHelperLM.chunk(c.getX() + radius);
        int maxZ = MathHelperLM.chunk(c.getZ() + radius);
        return pos.posX >= minX && pos.posX <= maxX && pos.posZ >= minZ && pos.posZ <= maxZ;
    }

    public static boolean isInSpawnD(int dim, double x, double z)
    {
        return dim == 0 && isInSpawn(new ChunkDimPos(MathHelperLM.chunk(x), MathHelperLM.chunk(z), dim));
    }

    public static boolean isDimensionBlacklisted(GameProfile profile, int dim)
    {
        return !PermissionAPI.hasPermission(profile, FTBUPermissions.CLAIMS_DIMENSION_ALLOWED_PREFIX + dim, null);
    }

    public static boolean allowExplosion(World world, Explosion explosion)
    {
        ChunkDimPos pos = new ChunkDimPos(MathHelperLM.chunk(explosion.getPosition().xCoord), MathHelperLM.chunk(explosion.getPosition().zCoord), world.provider.getDimension());

        if(pos.dim == 0 && FTBUConfigWorld.SAFE_SPAWN.getBoolean() && isInSpawn(pos))
        {
            return false;
        }
        else
        {
            IForgePlayer owner = ClaimedChunkStorage.INSTANCE.getChunkOwner(pos);

            if(owner != null)
            {
                EnumEnabled fe = (EnumEnabled) RankConfigAPI.getRankConfig(owner.getProfile(), FTBUPermissions.CLAIMS_FORCED_EXPLOSIONS).getValue();

                if(fe == null)
                {
                    return owner.getTeam() == null || !FTBUTeamData.get(owner.getTeam()).disableExplosions();
                }
                else
                {
                    return fe == EnumEnabled.ENABLED;
                }
            }

            return true;
        }
    }

    public static boolean claimChunk(IForgePlayer player, ChunkDimPos pos)
    {
        if(!FTBUConfigWorld.CHUNK_CLAIMING.getBoolean())
        {
            return false;
        }

        if(isDimensionBlacklisted(player.getProfile(), pos.dim))
        {
            return false;
        }

        if(player.getTeam() == null)
        {
            EntityPlayerMP ep = player.getPlayer();

            if(ep != null)
            {
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.NO_TEAM);
            }

            return false;
        }

        //int max = RankConfigAPI.getRankConfig(player.getProfile(), FTBUPermissions.CLAIMS_MAX_CHUNKS).getInt();
        int max = FTBUConfigWorld.MAX_CLAIMED_CHUNKS.getInt();
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

        ClaimedChunk chunk = new ClaimedChunk(pos, player);
        ClaimedChunkStorage.INSTANCE.setChunk(pos, chunk);
        MinecraftForge.EVENT_BUS.post(new ChunkModifiedEvent.Claimed(chunk));
        return true;
    }

    public static boolean unclaimChunk(IForgePlayer player, ChunkDimPos pos)
    {
        if(FTBUConfigWorld.LOCKED_IN_CLAIMED_CHUNKS.getBoolean() && !FTBUConfigWorld.CHUNK_CLAIMING.getBoolean())
        {
            return false;
        }

        IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);

        if(chunk != null && chunk.getOwner().equalsPlayer(player))
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

        if(chunk == null || flag == chunk.isLoaded() || !player.equalsPlayer(chunk.getOwner()))
        {
            return false;
        }

        if(flag)
        {
            if(player.getTeam() == null)
            {
                EntityPlayerMP ep = player.getPlayer();

                if(ep != null)
                {
                    FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.NO_TEAM);
                }

                return false;
            }

            if(isDimensionBlacklisted(player.getProfile(), pos.dim))
            {
                return false;
            }

            //int max = RankConfigAPI.getRankConfig(player.getProfile(), FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();
            int max = FTBUConfigWorld.MAX_LOADED_CHUNKS.getInt();

            if(max == 0)
            {
                return false;
            }

            int loadedChunks = 0;

            for(IClaimedChunk c : ClaimedChunkStorage.INSTANCE.getChunks(player))
            {
                if(c.isLoaded())
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

        chunk.setLoaded(flag);
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
            FTBU.logger.info("Server restart in " + LMStringUtils.getTimeString(restartMillis));
        }

        FTBLibIntegration.API.ticking().add(this);

        LOCAL_BADGES.clear();
    }

    public void onLoadedBeforePlayers()
    {
        //ClaimedChunks.inst.chunks.clear();
    }

    public void onClosed()
    {
        ClaimedChunkStorage.INSTANCE.clear();
        LoadedChunkStorage.INSTANCE.clear();
        LOCAL_BADGES.clear();
        WebAPI.INST.stopAPI();
    }

    @Override
    public void writeData(NBTTagCompound nbt)
    {
        if(warps != null && !warps.isEmpty())
        {
            NBTTagCompound tag1 = new NBTTagCompound();

            for(Map.Entry<String, BlockDimPos> e : warps.entrySet())
            {
                tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
            }

            nbt.setTag("Warps", tag1);
        }

        nbt.setTag("Chunks", ClaimedChunkStorage.INSTANCE.serializeNBT());
    }

    @Override
    public void readData(NBTTagCompound nbt)
    {
        nextChunkloaderUpdate = System.currentTimeMillis() + 10000L;

        if(nbt.hasKey("Warps"))
        {
            warps = new HashMap<>();

            NBTTagCompound nbt1 = (NBTTagCompound) nbt.getTag("Warps");

            if(nbt1 != null && !nbt1.hasNoTags())
            {
                for(String s1 : nbt1.getKeySet())
                {
                    setWarp(s1.toLowerCase(), new BlockDimPos(nbt1.getIntArray(s1)));
                }
            }
        }
        else
        {
            warps = null;
        }

        if(nbt.hasKey("Chunks", Constants.NBT.TAG_COMPOUND))
        {
            ClaimedChunkStorage.INSTANCE.deserializeNBT(nbt.getCompoundTag("Chunks"));
        }
    }

    @Override
    public void update()
    {
        long now = System.currentTimeMillis();

        if(restartMillis > 0L)
        {
            int secondsLeft = (int) ((restartMillis - System.currentTimeMillis()) / 1000L);
            String msg = LMStringUtils.getTimeString(secondsLeft * 1000L);

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
                    BroadcastSender.INSTANCE.addChatMessage(c);
                }
            }
        }

        if(Backups.INSTANCE.nextBackup > 0L && Backups.INSTANCE.nextBackup <= now)
        {
            Backups.INSTANCE.run(LMServerUtils.getServer());
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
    }

    public Collection<String> listWarps()
    {
        if(warps == null || warps.isEmpty())
        {
            return Collections.emptySet();
        }

        return warps.keySet();
    }

    public BlockDimPos getWarp(String s)
    {
        return warps == null ? null : warps.get(s);
    }

    public boolean setWarp(String s, BlockDimPos pos)
    {
        if(pos == null)
        {
            return warps != null && warps.remove(s) != null;
        }

        if(warps == null)
        {
            warps = new HashMap<>();
        }

        return warps.put(s, pos.copy()) == null;
    }

    public int warpsSize()
    {
        return warps == null ? 0 : warps.size();
    }
}
