package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.team.ForgeTeamPlayerLeftEvent;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamSettingsEvent;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LatvianModder on 01.06.2016.
 */
public class FTBUTeamEventHandler
{
    /*@SubscribeEvent
    public static void onDataSynced(ForgeTeamEvent.Sync event)
    {
        if(event.team.hasCapability(FTBUCapabilities.FTBU_TEAM_DATA, null))
        {
            FTBUTeamData data = event.team.getCapability(FTBUCapabilities.FTBU_TEAM_DATA, null);

            if(event.team.world.getSide().isServer())
            {
                NBTTagCompound tag = new NBTTagCompound();
                data.toMP().writeSyncData(event.team, tag, event.player);
                event.data.setTag("FTBU", tag);
            }
            else
            {
                data.toSP().readSyncData(event.team, event.data.getCompoundTag("FTBU"), event.player);
            }
        }
    }*/

    @SubscribeEvent
    public static void getSettings(ForgeTeamSettingsEvent event)
    {
        FTBUTeamData data = FTBUTeamData.get(event.getTeam());

        if(data != null)
        {
            data.addConfig(event.getSettings());
        }
    }

    @SubscribeEvent
    public static void onPlayerLeft(ForgeTeamPlayerLeftEvent event)
    {
        FTBUTeamData data = FTBUTeamData.get(event.getTeam());

        if(data != null)
        {
            FTBUUniverseData.unclaimAllChunks(event.getPlayer(), null);
        }
    }
}