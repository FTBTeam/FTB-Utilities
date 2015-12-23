package latmod.ftbu.mod.handlers.ftbl;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.*;
import latmod.ftbu.api.EventLMWorldClient;
import latmod.ftbu.api.guide.ClientGuideFile;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.world.LMWorldClient;

@SideOnly(Side.CLIENT)
public class FTBLIntegrationClient extends FTBLIntegrationCommon
{
	public void onReloadedClient(EventFTBReload e)
	{
		FTBUClient.onReloaded();
	}
	
	public void onModeSetClient(EventFTBModeSet e)
	{
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