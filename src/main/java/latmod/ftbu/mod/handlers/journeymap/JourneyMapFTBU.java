package latmod.ftbu.mod.handlers.journeymap;

import ftb.lib.FTBLib;
import journeymap.client.api.*;
import journeymap.client.api.event.ClientEvent;

/**
 * Created by LatvianModder on 17.01.2016.
 */
@ClientPlugin
public class JourneyMapFTBU implements IClientPlugin
{
	public void initialize(IClientAPI api)
	{
		FTBLib.logger.info("FTBUtilities <-> JourneyMap Integration loaded");
	}
	
	public void onEvent(ClientEvent event)
	{
	}
}
