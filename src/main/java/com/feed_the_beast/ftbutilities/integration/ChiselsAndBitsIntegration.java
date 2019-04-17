package com.feed_the_beast.ftbutilities.integration;

import com.feed_the_beast.ftblib.lib.util.InvUtils;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import mod.chiselsandbits.api.EventBlockBitModification;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class ChiselsAndBitsIntegration
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(ChiselsAndBitsIntegration.class);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChiselEvent(EventBlockBitModification event)
	{
		if (ClaimedChunks.blockBlockEditing(event.getPlayer(), event.getPos(), null))
		{
			InvUtils.forceUpdate(event.getPlayer());
			event.setCanceled(true);
		}
	}
}