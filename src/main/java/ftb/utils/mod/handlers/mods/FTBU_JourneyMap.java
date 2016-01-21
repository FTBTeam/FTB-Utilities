package ftb.utils.mod.handlers.mods;

import ftb.lib.FTBLib;
import journeymap.client.api.*;
import journeymap.client.api.event.ClientEvent;

/**
 * Created by LatvianModder on 17.01.2016.
 */
@ClientPlugin
public class FTBU_JourneyMap implements IClientPlugin
{
	public void initialize(IClientAPI api)
	{
		//TODO: Later
		FTBLib.logger.info("FTBUtilities <-> JourneyMap Integration loaded");
	}
	
	public void onEvent(ClientEvent event)
	{
	}
}
