package latmod.ftbu.mod.client;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.EventPlayerActionButtons;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.lib.config.ConfigEntryBool;

@SideOnly(Side.CLIENT)
public class FTBUGuiEventHandler
{
	public static final FTBUGuiEventHandler instance = new FTBUGuiEventHandler();
	
	public static final ConfigEntryBool button_guide = new ConfigEntryBool("guide", true);
	public static final ConfigEntryBool button_info = new ConfigEntryBool("info", true);
	public static final ConfigEntryBool button_claims = new ConfigEntryBool("claims", true);
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEvent(EventPlayerActionButtons e)
	{
		if(e.self && LatCoreMCClient.isPlaying())
		{
			e.actions.add(FTBUActions.friends_gui);
			if(e.addAll || button_guide.get()) e.actions.add(FTBUActions.guide);
			if(e.addAll || button_info.get()) e.actions.add(FTBUActions.info);
			if(e.addAll || button_claims.get()) e.actions.add(FTBUActions.claims);
		}
	}
}