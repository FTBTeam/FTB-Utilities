package latmod.ftbu.mod.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.api.*;
import latmod.ftbu.api.guide.ClientGuideFile;
import latmod.ftbu.mod.FTBUTicks;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.ftbu.world.*;

public class FTBULibEventHandler
{
	@SubscribeEvent
	public void onReloadedPre(EventFTBReloadPre e)
	{
		if(e.side.isClient()) return;
		
		if(FTBUConfigGeneral.restartTimer.get() > 0)
			FTBUTicks.serverStarted();
		
		for(LMPlayer p : LMWorldServer.inst.players)
			p.toPlayerMP().refreshStats();
	}
	
	@SubscribeEvent
	public void onModeChanged(EventFTBModeSet e)
	{
		if(e.side.isClient()) ClientGuideFile.instance.reload(e);
		
		FTBUConfigGeneral.onReloaded(e.side);
	}
}