package latmod.ftbu.mod.client;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.*;
import latmod.ftbu.api.EventLMWorldClient;
import latmod.ftbu.api.guide.ClientGuideFile;
import latmod.ftbu.mod.FTBLIntegration;
import latmod.ftbu.world.LMWorldClient;

@SideOnly(Side.CLIENT)
public class FTBLIntegrationClient extends FTBLIntegration
{
	public void onReloaded(EventFTBReload e)
	{
		super.onReloaded(e);
		
		if(e.side.isClient())
			FTBUClient.onReloaded();
	}
	
	public void onModeSet(EventFTBModeSet e)
	{
		super.onModeSet(e);
		
		if(e.side.isClient())
			ClientGuideFile.instance.reload(e);
	}
	
	public void onFTBWorldClient(EventFTBWorldClient e)
	{
		if(e.world == null)
		{
			FTBUClient.onWorldClosed();
			new EventLMWorldClient.Closed(LMWorldClient.inst).post();
			LMWorldClient.inst = null;
		}
		else if(e.isFake)
		{
			LMWorldClient.inst = new LMWorldClient(0);
		}
	}
}