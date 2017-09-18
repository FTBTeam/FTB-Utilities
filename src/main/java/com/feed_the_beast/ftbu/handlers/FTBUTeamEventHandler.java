package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamConfigEvent;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamPlayerLeftEvent;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
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
	public static void getSettings(ForgeTeamConfigEvent event)
	{
		FTBUTeamData.get(event.getTeam()).addConfig(event);
	}

	@SubscribeEvent
	public static void onPlayerLeft(ForgeTeamPlayerLeftEvent event)
	{
		FTBUUniverseData.unclaimAllChunks(event.getPlayer(), null);
	}
}