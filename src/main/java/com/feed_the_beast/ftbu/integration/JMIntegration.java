package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.gui.ClientClaimedChunks;
import com.feed_the_beast.ftbu.gui.UpdateClientDataEvent;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksRequest;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksUpdate;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@ClientPlugin
public class JMIntegration implements IClientPlugin
{
	private static IClientAPI clientAPI;
	private static final Map<ChunkPos, PolygonOverlay> POLYGONS = new HashMap<>();
	static ChunkPos lastPosition = null;

	@Override
	public void initialize(IClientAPI api)
	{
		clientAPI = api;
		api.subscribe(getModId(), EnumSet.of(ClientEvent.Type.DISPLAY_UPDATE, ClientEvent.Type.MAPPING_STOPPED));
		MinecraftForge.EVENT_BUS.register(JMIntegration.class);
	}

	@Override
	public String getModId()
	{
		return FTBUFinals.MOD_ID;
	}

	@Override
	public void onEvent(ClientEvent event)
	{
		switch (event.type)
		{
			case DISPLAY_UPDATE:
				if (ClientUtils.MC.player != null)
				{
					new MessageClaimedChunksRequest(ClientUtils.MC.player).sendToServer();
				}
				break;
			case MAPPING_STOPPED:
				clearData();
				break;
		}
	}

	private static void clearData()
	{
		if (!POLYGONS.isEmpty())
		{
			POLYGONS.clear();
			clientAPI.removeAll(FTBUFinals.MOD_ID);
		}
	}

	static void chunkChanged(ChunkPos pos, @Nullable ClientClaimedChunks.ChunkData chunk)
	{
		if (!POLYGONS.isEmpty() && (!FTBUClientConfig.general.journeymap_overlay || !clientAPI.playerAccepts(FTBUFinals.MOD_ID, DisplayType.Polygon)))
		{
			clearData();
			return;
		}

		try
		{
			PolygonOverlay p = POLYGONS.get(pos);

			if (p != null)
			{
				clientAPI.remove(p);

				if (chunk == null)
				{
					POLYGONS.remove(pos);
				}
			}

			if (chunk != null)
			{
				int dim = 0;

				MapPolygon poly = PolygonHelper.createChunkPolygon(pos.x, 0, pos.z);
				ShapeProperties shapeProperties = new ShapeProperties();

				shapeProperties.setFillOpacity(0.2F);
				shapeProperties.setStrokeOpacity(0F);

				shapeProperties.setFillColor(chunk.team.color.getColor().rgba());

				p = new PolygonOverlay(FTBUFinals.MOD_ID, "claimed_" + pos.x + '_' + pos.z, dim, shapeProperties, poly);
				p.setOverlayGroupName("claimed_chunks").setTitle(chunk.team.formattedName + '\n' + TextFormatting.GREEN + FTBULang.CHUNKS_CLAIMED_AREA.translate());
				POLYGONS.put(pos, p);
				clientAPI.show(p);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

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
			new MessageClaimedChunksRequest(ClientUtils.MC.player).sendToServer();
		}
	}

	@SubscribeEvent
	public static void onDataReceived(UpdateClientDataEvent event)
	{
		MessageClaimedChunksUpdate m = event.getMessage();
		ClientClaimedChunks.ChunkData data[] = new ClientClaimedChunks.ChunkData[ChunkSelectorMap.TILES_GUI * ChunkSelectorMap.TILES_GUI];

		for (ClientClaimedChunks.Team team : m.teams.values())
		{
			for (Map.Entry<Integer, ClientClaimedChunks.ChunkData> entry : team.chunks.entrySet())
			{
				int x = entry.getKey() % ChunkSelectorMap.TILES_GUI;
				int z = entry.getKey() / ChunkSelectorMap.TILES_GUI;
				data[x + z * ChunkSelectorMap.TILES_GUI] = entry.getValue();
			}
		}

		for (int z = 0; z < ChunkSelectorMap.TILES_GUI; z++)
		{
			for (int x = 0; x < ChunkSelectorMap.TILES_GUI; x++)
			{
				JMIntegration.chunkChanged(new ChunkPos(m.startX + x, m.startZ + z), data[x + z * ChunkSelectorMap.TILES_GUI]);
			}
		}
	}
}