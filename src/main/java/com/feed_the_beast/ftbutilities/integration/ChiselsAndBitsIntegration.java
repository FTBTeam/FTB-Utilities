package com.feed_the_beast.ftbutilities.integration;

import com.feed_the_beast.ftblib.lib.OtherMods;
import com.feed_the_beast.ftblib.lib.util.InvUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import mod.chiselsandbits.api.EventBlockBitModification;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class ChiselsAndBitsIntegration
{
	@Optional.Method(modid = OtherMods.CHISELS_AND_BITS)
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