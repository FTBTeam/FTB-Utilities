package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IUniverse;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.badges.BadgeStorage;
import com.feed_the_beast.ftbu.cmd.CmdRestart;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.backups.Backups;
import com.google.gson.JsonArray;
import com.latmod.lib.BroadcastSender;
import com.latmod.lib.EnumEnabled;
import com.latmod.lib.math.BlockDimPos;
import com.latmod.lib.math.ChunkDimPos;
import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMJsonUtils;
import com.latmod.lib.util.LMServerUtils;
import com.latmod.lib.util.LMStringUtils;
import com.latmod.lib.util.LMUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 18.05.2016.
 */
public class FTBUUniverseData implements ICapabilitySerializable<NBTTagCompound>, ITickable
{
    public static final BadgeStorage LOCAL_BADGES = new BadgeStorage();
    public long restartMillis;
    private long nextChunkloaderUpdate;
    private Map<String, BlockDimPos> warps;
    private String lastRestartMessage;

    public static FTBUUniverseData get(IUniverse u)
    {
        return u.hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null) ? u.getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null) : null;
    }

    @Override
    public final boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == FTBUCapabilities.FTBU_WORLD_DATA;
    }

    @Override
    public final <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == FTBUCapabilities.FTBU_WORLD_DATA)
        {
            return (T) this;
        }

        return null;
    }

    // Override //

    public static Badge getServerBadge(IForgePlayer p)
    {
        if(p == null)
        {
            return null;
        }

        Badge b = LOCAL_BADGES.badgePlayerMap.get(p.getProfile().getId());
        if(b != null)
        {
            return b;
        }

        String rank = Ranks.INSTANCE.getRankOf(p.getProfile()).badge;
        if(!rank.isEmpty())
        {
            b = LOCAL_BADGES.badgeMap.get(rank);
            if(b != null)
            {
                return b;
            }
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

        if(pos.dim != 0 || (!server.isDedicatedServer() && !FTBUConfigWorld.spawn_area_in_sp.getAsBoolean()))
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
        JsonArray a = FTBUPermissions.CLAIMS_DIMENSION_BLACKLIST.getJson(profile).getAsJsonArray();

        for(int i = 0; i < a.size(); i++)
        {
            if(a.get(i).getAsInt() == dim)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean allowExplosion(World world, Explosion explosion)
    {
        ChunkDimPos pos = new ChunkDimPos(MathHelperLM.chunk(explosion.getPosition().xCoord), MathHelperLM.chunk(explosion.getPosition().zCoord), world.provider.getDimension());

        if(pos.dim == 0 && FTBUConfigWorld.safe_spawn.getAsBoolean() && isInSpawn(pos))
        {
            return false;
        }
        else
        {
            IForgePlayer owner = FTBUtilitiesAPI.get().getClaimedChunks().getChunkOwner(pos);

            if(owner != null)
            {
                EnumEnabled fe = FTBUPermissions.CLAIMS_FORCED_EXPLOSIONS.get(owner.getProfile());

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
        if(isDimensionBlacklisted(player.getProfile(), pos.dim))
        {
            return false;
        }

        if(player.getTeam() == null)
        {
            EntityPlayerMP ep = player.getPlayer();

            if(ep != null)
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.NO_TEAM);
            }

            return false;
        }

        int max = FTBUPermissions.CLAIMS_MAX_CHUNKS.get(player.getProfile());
        if(max == 0)
        {
            return false;
        }

        if(FTBUtilitiesAPI.get().getClaimedChunks().getClaimedChunks(player) >= max)
        {
            return false;
        }

        IForgePlayer chunkOwner = FTBUtilitiesAPI.get().getClaimedChunks().getChunkOwner(pos);

        if(chunkOwner != null)
        {
            return false;
        }

        FTBUtilitiesAPI.get().getClaimedChunks().setOwner(pos, player);
        return true;
    }

    public static boolean unclaimChunk(IForgePlayer player, ChunkDimPos pos)
    {
        IForgePlayer chunkOwner = FTBUtilitiesAPI.get().getClaimedChunks().getChunkOwner(pos);

        if(chunkOwner != null && chunkOwner.equalsPlayer(player))
        {
            setLoaded(player, pos, false);
            FTBUtilitiesAPI.get().getClaimedChunks().setOwner(pos, null);
            return true;
        }

        return false;
    }

    public static void unclaimAllChunks(IForgePlayer player, @Nullable Integer dim)
    {
        for(ChunkDimPos chunk : FTBUtilitiesAPI.get().getClaimedChunks().getChunks(player))
        {
            if(dim == null || dim.intValue() == chunk.dim)
            {
                setLoaded(player, chunk, false);
                FTBUtilitiesAPI.get().getClaimedChunks().setOwner(chunk, null);
            }
        }
    }

    public static boolean setLoaded(IForgePlayer player, ChunkDimPos pos, boolean flag)
    {
        if(flag ? !FTBUtilitiesAPI.get().getLoadedChunks().isLoaded(pos, null) : FTBUtilitiesAPI.get().getLoadedChunks().isLoaded(pos, player))
        {
            if(flag)
            {
                if(player.getTeam() == null)
                {
                    EntityPlayerMP ep = player.getPlayer();

                    if(ep != null)
                    {
                        FTBLibAPI.get().sendNotification(ep, FTBUNotifications.NO_TEAM);
                    }

                    return false;
                }

                if(isDimensionBlacklisted(player.getProfile(), pos.dim))
                {
                    return false;
                }

                int max = FTBUPermissions.CHUNKLOADER_MAX_CHUNKS.get(player.getProfile());
                if(max == 0 || FTBUtilitiesAPI.get().getLoadedChunks().getLoadedChunks(player) >= max)
                {
                    return false;
                }
            }

            FTBUtilitiesAPI.get().getLoadedChunks().setLoaded(pos, flag ? player : null);
            FTBUtilitiesAPI.get().getLoadedChunks().checkUnloaded(pos.dim);

            if(player.getPlayer() != null)
            {
                new MessageAreaUpdate(pos.posX, pos.posZ, pos.dim, 1, 1).sendTo(player.getPlayer());
            }

            return true;
        }

        return false;
    }

    public static boolean canInteract(IForgePlayer owner, IForgePlayer player, boolean leftClick, BlockPos blockPos)
    {
        if(owner.equalsPlayer(player))
        {
            return true;
        }
        else if(owner.getTeam() == null)
        {
            return true;
        }
        else if(player.isFake())
        {
            return FTBUTeamData.get(owner.getTeam()).allowFakePlayers();
        }
        /*else if(p.isOnline() && PermissionAPI.hasPermission(p.getProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(p.getPlayer(), pos)))
        {
            return true;
        }*/
        else if(player.isOnline() && player.getPlayer().capabilities.isCreativeMode)
        {
            return true;
        }

        return owner.getTeam().getStatus(player).isAlly();
    }

    public void onLoaded()
    {
        ClaimedChunkStorage.INSTANCE = new ClaimedChunkStorage();

        long startMillis = System.currentTimeMillis();
        Backups.INSTANCE.nextBackup = startMillis + FTBUConfigBackups.backupMillis();
        lastRestartMessage = "";

        if(FTBUConfigGeneral.auto_restart.getAsBoolean() && FTBUConfigGeneral.restart_timer.getAsInt() > 0)
        {
            restartMillis = startMillis + (long) (FTBUConfigGeneral.restart_timer.getAsInt() * 3600D * 1000D);
            FTBU.logger.info("Server restart in " + LMStringUtils.getTimeString(restartMillis));
        }

        FTBLibAPI.get().getRegistries().tickables().add(this);

        LOCAL_BADGES.clear();
    }

    public void onLoadedBeforePlayers()
    {
        //ClaimedChunks.inst.chunks.clear();
    }

    public void onClosed()
    {
        ClaimedChunkStorage.INSTANCE = null;
        LOCAL_BADGES.clear();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        if(warps != null && !warps.isEmpty())
        {
            NBTTagCompound tag1 = new NBTTagCompound();

            for(Map.Entry<String, BlockDimPos> e : warps.entrySet())
            {
                tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
            }

            tag.setTag("Warps", tag1);
        }

        tag.setTag("Chunks", FTBUtilitiesAPI.get().getClaimedChunks().serializeNBT());

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        nextChunkloaderUpdate = System.currentTimeMillis() + 10000L;

        if(tag.hasKey("Warps"))
        {
            warps = new HashMap<>();

            NBTTagCompound tag1 = (NBTTagCompound) tag.getTag("Warps");

            if(tag1 != null && !tag1.hasNoTags())
            {
                for(String s1 : tag1.getKeySet())
                {
                    setWarp(s1.toLowerCase(), new BlockDimPos(tag1.getIntArray(s1)));
                }
            }
        }
        else
        {
            warps = null;
        }

        FTBUtilitiesAPI.get().getClaimedChunks().deserializeNBT(tag.getTagList("Chunks", Constants.NBT.TAG_INT_ARRAY));
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
            FTBUtilitiesAPI.get().getLoadedChunks().checkUnloaded(null);
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
