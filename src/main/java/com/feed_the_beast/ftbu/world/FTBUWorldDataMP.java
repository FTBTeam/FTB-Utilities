package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.notification.Notification;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.BroadcastSender;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbl.util.LMNBTUtils;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.badges.BadgeStorage;
import com.feed_the_beast.ftbu.cmd.CmdRestart;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.handlers.FTBUChunkEventHandler;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.google.gson.JsonArray;
import com.latmod.lib.EnumEnabled;
import com.latmod.lib.json.LMJsonUtils;
import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMStringUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUWorldDataMP extends FTBUWorldData implements ITickable, INBTSerializable<NBTTagCompound>
{
    public static final BadgeStorage localBadges = new BadgeStorage();
    public static ClaimedChunkStorage chunks;
    public long restartMillis;
    private long nextChunkloaderUpdate;
    private Map<String, BlockDimPos> warps;
    private String lastRestartMessage;

    public static Badge getServerBadge(ForgePlayerMP p)
    {
        if(p == null)
        {
            return null;
        }

        Badge b = localBadges.badgePlayerMap.get(p.getProfile().getId());
        if(b != null)
        {
            return b;
        }

        String rank = Ranks.instance().getRankOf(p.getProfile()).badge;
        if(!rank.isEmpty())
        {
            b = localBadges.badgeMap.get(rank);
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
            localBadges.clear();
            localBadges.loadBadges(LMJsonUtils.fromJson(new File(FTBLib.folderLocal, "ftbu/badges.json")));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static boolean isInSpawn(@Nonnull ChunkDimPos pos)
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
        ChunkDimPos pos = new ChunkDimPos(world.provider.getDimension(), MathHelperLM.chunk(explosion.getPosition().xCoord), MathHelperLM.chunk(explosion.getPosition().zCoord));

        if(pos.dim == 0 && FTBUConfigGeneral.safe_spawn.getAsBoolean() && FTBUWorldDataMP.isInSpawn(pos))
        {
            return false;
        }
        else
        {
            ClaimedChunk c = FTBUWorldDataMP.chunks.getChunk(pos);

            if(c != null)
            {
                EnumEnabled fe = FTBUPermissions.CLAIMS_FORCED_EXPLOSIONS.get(c.owner.getProfile());

                if(fe == null)
                {

                    return !c.owner.hasTeam() || !FTBUTeamData.get(c.owner.getTeam()).toMP().disable_explosions.getAsBoolean();
                }
                else
                {
                    return fe == EnumEnabled.ENABLED;
                }
            }

            return true;
        }
    }

    public static boolean claimChunk(ForgePlayerMP player, ChunkDimPos pos)
    {
        if(isDimensionBlacklisted(player.getProfile(), pos.dim))
        {
            return false;
        }

        if(!player.hasTeam())
        {
            EntityPlayerMP ep = player.getPlayer();

            if(ep != null)
            {
                Notification.error("no_team", FTBLibLang.team_no_team.textComponent()).sendTo(ep);
            }

            return false;
        }

        int max = FTBUPermissions.CLAIMS_MAX_CHUNKS.get(player.getProfile());
        if(max == 0)
        {
            return false;
        }

        if(chunks.getClaimedChunks(player.getProfile().getId()) >= max)
        {
            return false;
        }

        ClaimedChunk chunk = chunks.getChunk(pos);

        if(chunk != null)
        {
            return false;
        }

        chunks.put(pos, new ClaimedChunk(player.getWorld(), player, pos));
        player.sendUpdate();

        return true;
    }

    public static boolean unclaimChunk(@Nonnull ForgePlayerMP player, @Nonnull ChunkDimPos pos)
    {
        ClaimedChunk chunk = chunks.getChunk(pos);

        if(chunk != null && chunk.isChunkOwner(player))
        {
            setLoaded(player, pos, false);
            chunks.put(pos, null);
            player.sendUpdate();
            return true;
        }

        return false;
    }

    public static void unclaimAllChunks(@Nonnull ForgePlayerMP player, @Nullable Integer dim)
    {
        Collection<ClaimedChunk> ch = new HashSet<>();
        ch.addAll(chunks.getChunks(player.getProfile().getId()));

        if(!ch.isEmpty())
        {
            for(ClaimedChunk c : ch)
            {
                if(dim == null || dim.intValue() == c.pos.dim)
                {
                    setLoaded(player, c.pos, false);
                    chunks.put(c.pos, null);
                }
            }

            player.sendUpdate();
        }
    }

    public static boolean setLoaded(@Nonnull ForgePlayerMP player, @Nonnull ChunkDimPos pos, boolean flag)
    {
        ClaimedChunk chunk = chunks.getChunk(pos);

        if(chunk != null && flag != chunk.loaded && player.equalsPlayer(chunk.owner))
        {
            if(flag)
            {
                if(!player.hasTeam())
                {
                    EntityPlayerMP ep = player.getPlayer();

                    if(ep != null)
                    {
                        Notification.error("no_team", FTBLibLang.team_no_team.textComponent()).sendTo(ep);
                    }

                    return false;
                }

                if(isDimensionBlacklisted(player.getProfile(), pos.dim))
                {
                    return false;
                }

                int max = FTBUPermissions.CHUNKLOADER_MAX_CHUNKS.get(player.getProfile());
                if(max == 0 || chunks.getLoadedChunks(player.getProfile().getId()) >= max)
                {
                    return false;
                }
            }

            chunk.loaded = flag;
            FTBUChunkEventHandler.instance.markDirty(LMDimUtils.getWorld(pos.dim));

            if(player.getPlayer() != null)
            {
                new MessageAreaUpdate(pos.chunkXPos, pos.chunkZPos, pos.dim, 1, 1).sendTo(player.toMP().getPlayer());
                player.sendUpdate();
            }

            return true;
        }

        return false;
    }

    @Override
    public FTBUWorldDataMP toMP()
    {
        return this;
    }

    @Override
    public void onLoaded()
    {
        chunks = new ClaimedChunkStorage();

        long startMillis = System.currentTimeMillis();
        Backups.nextBackup = startMillis + FTBUConfigBackups.backupMillis();
        lastRestartMessage = "";

        if(FTBUConfigGeneral.restart_timer.getAsInt() > 0)
        {
            restartMillis = startMillis + (long) (FTBUConfigGeneral.restart_timer.getAsInt() * 3600D * 1000D);
            FTBU.logger.info("Server restart in " + LMStringUtils.getTimeString(restartMillis));
        }

        FTBLib.registerServerTickable(FTBLib.getServer(), this);

        localBadges.clear();
    }

    @Override
    public void onLoadedBeforePlayers()
    {
        //ClaimedChunks.inst.chunks.clear();
    }

    @Override
    public void onClosed()
    {
        chunks = null;
        localBadges.clear();
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
                for(String s1 : LMNBTUtils.getMapKeys(tag1))
                {
                    setWarp(s1.toLowerCase(), new BlockDimPos(tag1.getIntArray(s1)));
                }
            }
        }
        else
        {
            warps = null;
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
                    ITextComponent c = FTBULang.timer_restart.textComponent(msg);
                    c.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
                    BroadcastSender.inst.addChatMessage(c);
                }
            }
        }

        if(Backups.nextBackup > 0L && Backups.nextBackup <= now)
        {
            Backups.run(FTBLib.getServer());
        }

        if(nextChunkloaderUpdate < now)
        {
            nextChunkloaderUpdate = now + 2L * 3600L;
            FTBUChunkEventHandler.instance.markDirty(null);
        }

        if(Backups.thread != null && Backups.thread.isDone)
        {
            Backups.thread = null;
            Backups.postBackup();
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
