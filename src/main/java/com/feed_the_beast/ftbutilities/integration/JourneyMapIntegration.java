package com.feed_the_beast.ftbutilities.integration;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClientConfig;
import com.feed_the_beast.ftbutilities.gui.ClientClaimedChunks;
import com.feed_the_beast.ftbutilities.gui.UpdateClientDataEvent;
import com.feed_the_beast.ftbutilities.net.MessageClaimedChunksRequest;
import com.feed_the_beast.ftbutilities.net.MessageClaimedChunksUpdate;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.client.resources.I18n;
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
//Disabled integration for now
//@ClientPlugin
public class JourneyMapIntegration implements IClientPlugin
{
	private static IClientAPI clientAPI;
	private static final Map<ChunkPos, PolygonOverlay> POLYGONS = new HashMap<>();
	static ChunkPos lastPosition = null;

	@Override
	public void initialize(IClientAPI api)
	{
		clientAPI = api;
		api.subscribe(getModId(), EnumSet.of(ClientEvent.Type.DISPLAY_UPDATE, ClientEvent.Type.MAPPING_STOPPED));
		MinecraftForge.EVENT_BUS.register(JourneyMapIntegration.class);
	}

	@Override
	public String getModId()
	{
		return FTBUtilities.MOD_ID;
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
			clientAPI.removeAll(FTBUtilities.MOD_ID);
		}
	}

	private static void chunkChanged(ChunkPos pos, @Nullable ClientClaimedChunks.ChunkData chunk, int dim)
	{
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
				MapPolygon poly = PolygonHelper.createChunkPolygon(pos.x, 0, pos.z);
				p = new PolygonOverlay(FTBUtilities.MOD_ID, "claimed_" + pos.x + '_' + pos.z, dim, (ShapeProperties) chunk.team.shapeProperties, poly);
				p.setOverlayGroupName("claimed_chunks");
				p.setTitle(chunk.team.formattedName + '\n' + TextFormatting.GREEN + I18n.format("ftbutilities.lang.chunks.claimed_area"));
				/*
				DEFAULT_SCALE = 1.0F;
   				DEFAULT_COLOR = 16777215;
   				DEFAULT_OPACITY = 1.0F;
   				DEFAULT_BACKGROUND_COLOR = 0;
   				DEFAULT_BACKGROUND_OPACITY = 0.7F;
				*/
				p.getTextProperties().setOffsetX(TextProperties.DEFAULT_OFFSET_X);
				p.getTextProperties().setOffsetY(TextProperties.DEFAULT_OFFSET_Y);
				p.getTextProperties().setMinZoom(TextProperties.DEFAULT_MIN_ZOOM);
				p.getTextProperties().setMaxZoom(TextProperties.DEFAULT_MAX_ZOOM);
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
		if (!FTBUtilitiesClientConfig.general.journeymap_overlay || event.getEntity() != ClientUtils.MC.player)
		{
			return;
		}

		if (JourneyMapIntegration.lastPosition == null || MathUtils.dist(event.getNewChunkX(), event.getNewChunkZ(), JourneyMapIntegration.lastPosition.x, JourneyMapIntegration.lastPosition.z) >= 3D)
		{
			JourneyMapIntegration.lastPosition = new ChunkPos(event.getNewChunkX(), event.getNewChunkZ());
			new MessageClaimedChunksRequest(ClientUtils.MC.player).sendToServer();
		}
	}

	@SubscribeEvent
	public static void onDataReceived(UpdateClientDataEvent event)
	{
		if (!POLYGONS.isEmpty() && (!FTBUtilitiesClientConfig.general.journeymap_overlay || !clientAPI.playerAccepts(FTBUtilities.MOD_ID, DisplayType.Polygon)))
		{
			clearData();
			return;
		}

		MessageClaimedChunksUpdate m = event.getMessage();
		ClientClaimedChunks.ChunkData data[] = new ClientClaimedChunks.ChunkData[ChunkSelectorMap.TILES_GUI * ChunkSelectorMap.TILES_GUI];

		for (ClientClaimedChunks.Team team : m.teams.values())
		{
			ShapeProperties shapeProperties = new ShapeProperties();
			shapeProperties.setFillOpacity(0.2F);
			shapeProperties.setStrokeOpacity(0F);
			shapeProperties.setFillColor(team.color.getColor().rgba());
			team.shapeProperties = shapeProperties;

			for (Map.Entry<Integer, ClientClaimedChunks.ChunkData> entry : team.chunks.entrySet())
			{
				int x = entry.getKey() % ChunkSelectorMap.TILES_GUI;
				int z = entry.getKey() / ChunkSelectorMap.TILES_GUI;
				data[x + z * ChunkSelectorMap.TILES_GUI] = entry.getValue();
			}
		}

		int dim = ClientUtils.getDim();

		for (int z = 0; z < ChunkSelectorMap.TILES_GUI; z++)
		{
			for (int x = 0; x < ChunkSelectorMap.TILES_GUI; x++)
			{
				chunkChanged(new ChunkPos(m.startX + x, m.startZ + z), data[x + z * ChunkSelectorMap.TILES_GUI], dim);
			}
		}
	}
}