package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.BroadcastSender;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMNBTUtils;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.badges.BadgeStorage;
import com.feed_the_beast.ftbu.cmd.admin.CmdRestart;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.handlers.FTBUChunkEventHandler;
import com.feed_the_beast.ftbu.ranks.Ranks;
import latmod.lib.LMJsonUtils;
import latmod.lib.LMStringUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.INBTSerializable;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUWorldDataMP extends FTBUWorldData implements ITickable, INBTSerializable<NBTTagCompound>
{
    public static final BadgeStorage localBadges = new BadgeStorage();
    public long nextChunkloaderUpdate;
    public long restartMillis;
    private Map<String, BlockDimPos> warps;
    private long startMillis;
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

    @Override
    public FTBUWorldDataMP toMP()
    {
        return this;
    }

    @Override
    public void onLoaded()
    {
        ClaimedChunks.inst = new ClaimedChunks();

        startMillis = System.currentTimeMillis();
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
        ClaimedChunks.inst = null;
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
                    setWarp(s1, new BlockDimPos(tag1.getIntArray(s1)));
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
            return Collections.EMPTY_SET;
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
            return warps.remove(s) != null;
        }

        return warps.put(s, pos.copy()) == null;
    }

    public int warpsSize()
    {
        return warps == null ? 0 : warps.size();
    }
}
