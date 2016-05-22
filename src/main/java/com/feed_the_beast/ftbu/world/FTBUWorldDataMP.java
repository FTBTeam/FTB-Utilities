package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.FTBLibEventHandler;
import com.feed_the_beast.ftbl.api.IWorldTick;
import com.feed_the_beast.ftbl.util.BroadcastSender;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.badges.ServerBadges;
import com.feed_the_beast.ftbu.cmd.admin.CmdRestart;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.handlers.FTBUChunkEventHandler;
import latmod.lib.LMStringUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUWorldDataMP extends FTBUWorldData implements IWorldTick, INBTSerializable<NBTTagCompound>
{
    public Warps warps;
    public long nextChunkloaderUpdate;
    public long restartMillis;
    private long startMillis;
    private String lastRestartMessage;

    @Override
    public FTBUWorldDataMP toMP()
    {
        return this;
    }

    @Override
    public void onLoaded()
    {
        ClaimedChunks.inst = new ClaimedChunks();
        warps = new Warps();

        startMillis = System.currentTimeMillis();
        Backups.nextBackup = startMillis + FTBUConfigBackups.backupMillis();
        lastRestartMessage = "";

        if(FTBUConfigGeneral.restart_timer.getAsInt() > 0)
        {
            restartMillis = startMillis + (long) (FTBUConfigGeneral.restart_timer.getAsInt() * 3600D * 1000D);
            FTBU.logger.info("Server restart in " + LMStringUtils.getTimeString(restartMillis));
        }

        FTBLibEventHandler.ticking.add(this);
    }

    @Override
    public void onLoadedBeforePlayers()
    {
        ClaimedChunks.inst.chunks.clear();
    }

    @Override
    public void onClosed()
    {
        ClaimedChunks.inst = null;
        FTBLibEventHandler.ticking.remove(this);
    }

    @Override
    public void onTick(WorldServer w, long now)
    {
        if(w.provider.getDimensionType() == DimensionType.OVERWORLD)
        {
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

            if(ServerBadges.thread != null && ServerBadges.thread.isDone)
            {
                ServerBadges.thread = null;
                ServerBadges.sendToPlayer(null);
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        warps.writeToNBT(tag, "Warps");
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        nextChunkloaderUpdate = System.currentTimeMillis() + 10000L;
        warps.readFromNBT(tag, "Warps");
    }
}
