package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import mod.chiselsandbits.api.EventBlockBitModification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler(requiredMods = "chiselsandbits")
public class ChiselIntegration
{
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChiselEvent(EventBlockBitModification event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunkStorage.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getPlayer(), event.getHand(), new BlockPosContainer(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos())), event.isPlacing() ? BlockInteractionType.CNB_PLACE : BlockInteractionType.CNB_BREAK))
		{
			event.setCanceled(true);
		}
	}
}