package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FTBLIntegrationClient extends FTBLIntegration
{
    @Override
    public void onReloaded(ReloadEvent e)
    {
        super.onReloaded(e);

        if(e.world.getSide().isClient())
        {
            FTBLibClient.clearCachedData();

            //if(e.modeChanged)
            {
                //FIXME: GuideRepoList.reloadFromFolder(e.world.getMode());
            }
        }
    }
}