package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.client.FTBUClient;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Created by LatvianModder on 17.01.2016.
 */
@ParametersAreNonnullByDefault
@ClientPlugin
public class FTBU_JMPlugin implements IClientPlugin
{
    @Override
    public void initialize(final IClientAPI api)
    {
        FTBUClient.journeyMapHandler = new JMPluginHandler(api);
    }

    @Override
    public String getModId()
    {
        return FTBUFinals.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent event)
    {
        if(FTBUClient.journeyMapHandler != null)
        {
            switch(event.type)
            {
                case MAPPING_STARTED:
                    FTBUClient.journeyMapHandler.mappingStarted();
                    break;
                case MAPPING_STOPPED:
                    FTBUClient.journeyMapHandler.mappingStopped();
                    break;
            }
        }
    }
}
