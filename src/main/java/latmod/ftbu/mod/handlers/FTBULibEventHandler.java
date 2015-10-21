package latmod.ftbu.mod.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.api.EventFTBReloadPre;
import latmod.ftbu.mod.FTBUTicks;
import latmod.ftbu.mod.config.FTBUConfigGeneral;

public class FTBULibEventHandler
{
	@SubscribeEvent
	public void onReloadedPre(EventFTBReloadPre e)
	{
		if(e.side.isClient()) return;
		
		float prevRRTimer = FTBUConfigGeneral.restartTimer.get();
		if(FTBUConfigGeneral.restartTimer.get() > 0)
		{
			if(prevRRTimer != FTBUConfigGeneral.restartTimer.get())
				FTBUTicks.serverStarted();
		}
	}
}