package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.api.EventHandler;
import me.ichun.mods.ichunutil.api.event.BlockPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler(requiredMods = "iChunUtil;ichunutil")
public class IChunUtilIntegration
{
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockPickupEventEvent(BlockPickupEvent event)
	{
		/*if (event.getEntityPlayer() instanceof EntityPlayerMP && !ClaimedChunks.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.INTERACT))
		{
			event.setCanceled(true);
		}*/
	}
}