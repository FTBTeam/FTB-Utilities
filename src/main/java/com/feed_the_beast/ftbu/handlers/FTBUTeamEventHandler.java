package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.config.ConfigGroup;
import com.feed_the_beast.ftbl.api.events.ForgeTeamEvent;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.world.FTBUTeamDataMP;
import com.feed_the_beast.ftbu.world.FTBUTeamDataSP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LatvianModder on 01.06.2016.
 */
public class FTBUTeamEventHandler
{
    @SubscribeEvent
    public void attachCapabilities(ForgeTeamEvent.AttachCapabilities event)
    {
        event.addCapability(new ResourceLocation(FTBUFinals.MOD_ID, "data"), event.team.world.getSide().isServer() ? new FTBUTeamDataMP() : new FTBUTeamDataSP());
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
    public void getSettings(ForgeTeamEvent.GetSettings event)
    {
        if(event.team.hasCapability(FTBUCapabilities.FTBU_TEAM_DATA, null))
        {
            FTBUTeamDataMP data = event.team.getCapability(FTBUCapabilities.FTBU_TEAM_DATA, null).toMP();
            ConfigGroup group = new ConfigGroup();

            group.add("blocks", data.blocks);
            group.add("disable_explosions", data.disable_explosions);
            group.add("fake_players", data.fakePlayers);

            event.settings.add("ftbu", group);
        }
    }
}
