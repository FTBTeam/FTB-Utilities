package com.feed_the_beast.ftbutilities.integration;

import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftblib.lib.OtherMods;
import com.feed_the_beast.ftblib.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbutilities.data.BlockInteractionType;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import mod.chiselsandbits.api.EventBlockBitModification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler(requiredMods = OtherMods.CHISELS_AND_BITS)
public class ChiselsAndBitsIntegration
{
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChiselEvent(EventBlockBitModification event)
	{
		if (ClaimedChunks.isActive() && event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunks.instance.canPlayerInteract((EntityPlayerMP) event.getPlayer(), event.getHand(), new BlockPosContainer(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos())), event.isPlacing() ? BlockInteractionType.CNB_PLACE : BlockInteractionType.CNB_BREAK))
		{
			event.setCanceled(true);
		}
	}
}