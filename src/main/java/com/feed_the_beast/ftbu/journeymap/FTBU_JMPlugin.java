package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.client.FTBUClient;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;

/**
 * Created by LatvianModder on 17.01.2016.
 */
@ClientPlugin
public class FTBU_JMPlugin implements IClientPlugin
{
    @Override
    public void initialize(IClientAPI api)
    {
        FTBUClient.journeyMapHandler = new JMPluginHandler(api);
    }

    @Override
    public String getModId()
    { return FTBUFinals.MOD_ID; }

    @Override
    public void onEvent(ClientEvent event)
    {
        if(event.type == ClientEvent.Type.DISPLAY_UPDATE && FTBUClient.journeyMapHandler != null)
        {
            FTBUClient.journeyMapHandler.refresh(event.dimension);
        }
    }
}
