package ftb.utils.mod.handlers.jm;

import ftb.lib.FTBLib;
import ftb.utils.mod.FTBUFinals;
import ftb.utils.mod.client.FTBUClient;
import journeymap.client.api.*;
import journeymap.client.api.event.ClientEvent;

/**
 * Created by LatvianModder on 17.01.2016.
 */
@ClientPlugin
public class FTBU_JMPlugin implements IClientPlugin
{
	public void initialize(IClientAPI api)
	{
		FTBUClient.journeyMapHandler = new JMPluginHandler(api);
		FTBLib.logger.info("FTBUtilities <-> JourneyMap Integration loaded");
	}
	
	public String getModId()
	{ return FTBUFinals.MOD_ID; }
	
	public void onEvent(ClientEvent event)
	{
		if(event.type == ClientEvent.Type.DISPLAY_UPDATE && FTBUClient.journeyMapHandler != null)
		{
			FTBUClient.journeyMapHandler.refresh(event.dimension);
		}
	}
}
