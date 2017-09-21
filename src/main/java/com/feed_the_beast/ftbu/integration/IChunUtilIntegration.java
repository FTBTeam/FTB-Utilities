package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;

/**
 * @author LatvianModder
 */
@EventHandler(requiredMods = FTBLibFinals.ICHUN_UTIL)
public class IChunUtilIntegration
{
	/*@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockPickupEventEvent(BlockPickupEvent event)
	{
		if (event.getEntityPlayer() instanceof EntityPlayerMP && !ClaimedChunks.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.INTERACT))
		{
			event.setCanceled(true);
		}
	}*/
}