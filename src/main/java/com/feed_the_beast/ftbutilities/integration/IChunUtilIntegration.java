package com.feed_the_beast.ftbutilities.integration;


import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import me.ichun.mods.ichunutil.api.event.BlockEntityEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class IChunUtilIntegration
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(IChunUtilIntegration.class);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockPickupEvent(BlockEntityEvent.Pickup event)
	{
		if (event.getEntityLiving() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();

			for (BlockPos pos : event.getPoses())
			{
				if (ClaimedChunks.blockBlockEditing(player, pos, null))
				{
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockPlaceEvent(BlockEntityEvent.Place event)
	{
		if (event.getEntityLiving() instanceof EntityPlayerMP && ClaimedChunks.blockBlockEditing((EntityPlayerMP) event.getEntityLiving(), event.getPos(), event.getBlockState()))
		{
			event.setCanceled(true);
		}
	}
}