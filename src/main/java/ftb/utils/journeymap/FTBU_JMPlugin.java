package ftb.utils.journeymap;

import ftb.utils.FTBUFinals;
import ftb.utils.client.FTBUClient;
import journeymap.client.api.*;
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
