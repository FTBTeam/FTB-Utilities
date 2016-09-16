package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.team.AttachTeamCapabilitiesEvent;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamSettingsEvent;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LatvianModder on 01.06.2016.
 */
public class FTBUTeamEventHandler
{
    @SubscribeEvent
    public void attachCapabilities(AttachTeamCapabilitiesEvent event)
    {
        event.addCapability(new ResourceLocation(FTBUFinals.MOD_ID, "data"), new FTBUTeamData());
    }

    /*@SubscribeEvent
    public void onDataSynced(ForgeTeamEvent.Sync event)
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
    public void getSettings(ForgeTeamSettingsEvent event)
    {
        if(event.getTeam().hasCapability(FTBUCapabilities.FTBU_TEAM_DATA, null))
        {
            FTBUTeamData data = event.getTeam().getCapability(FTBUCapabilities.FTBU_TEAM_DATA, null);
            data.addConfig(event.getSettings());
        }
    }
}
