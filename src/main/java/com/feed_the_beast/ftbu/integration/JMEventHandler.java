package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.net.MessageJMRequest;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = "journeymap")
public class JMEventHandler
{
	@SubscribeEvent
	public static void onEnteringChunk(EntityEvent.EnteringChunk event)
	{
		if (!FTBUClientConfig.general.journeymap_overlay || event.getEntity() != ClientUtils.MC.player)
		{
			return;
		}

		if (JMIntegration.lastPosition == null || MathUtils.dist(event.getNewChunkX(), event.getNewChunkZ(), JMIntegration.lastPosition.x, JMIntegration.lastPosition.z) >= 3D)
		{
			JMIntegration.lastPosition = new ChunkPos(event.getNewChunkX(), event.getNewChunkZ());
			new MessageJMRequest(ClientUtils.MC.player).sendToServer();
		}
	}
}