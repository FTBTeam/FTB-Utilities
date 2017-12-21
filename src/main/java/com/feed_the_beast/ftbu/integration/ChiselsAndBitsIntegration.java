package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftblib.FTBLibFinals;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftblib.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbu.data.BlockInteractionType;
import com.feed_the_beast.ftbu.data.ClaimedChunks;
import mod.chiselsandbits.api.EventBlockBitModification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler(requiredMods = FTBLibFinals.CHISELS_AND_BITS)
public class ChiselsAndBitsIntegration
{
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChiselEvent(EventBlockBitModification event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunks.get().canPlayerInteract((EntityPlayerMP) event.getPlayer(), event.getHand(), new BlockPosContainer(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos())), event.isPlacing() ? BlockInteractionType.CNB_PLACE : BlockInteractionType.CNB_BREAK))
		{
			event.setCanceled(true);
		}
	}
}