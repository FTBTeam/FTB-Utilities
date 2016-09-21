package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.client.CachedClientData;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.latmod.lib.io.Bits;
import com.latmod.lib.util.LMUtils;
import gnu.trove.map.hash.TLongObjectHashMap;
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

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 17.01.2016.
 */
@ClientPlugin
public class JourneyMapIntegration implements IClientPlugin
{
    public static JourneyMapIntegration INST;
    private IClientAPI clientAPI;
    private TLongObjectHashMap<PolygonOverlay> polygons = new TLongObjectHashMap<>();

    @Override
    public void initialize(final IClientAPI api)
    {
        INST = this;
        clientAPI = api;
    }

    @Override
    public String getModId()
    {
        return FTBUFinals.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent event)
    {
        switch(event.type)
        {
            case DISPLAY_UPDATE:
                if(LMUtils.DEV_ENV)
                {
                    LMUtils.DEV_LOGGER.info("Display update: " + event);
                }
                break;
            case MAPPING_STOPPED:
                polygons.clear();
                clientAPI.removeAll(FTBUFinals.MOD_ID);
                break;
        }
    }

    public void chunkChanged(ChunkPos pos, @Nullable CachedClientData.ChunkData chunk)
    {
        try
        {
            long posLong = Bits.intsToLong(pos.chunkXPos, pos.chunkZPos);

            if(chunk != null && FTBUClientConfig.JOURNEYMAP_OVERLAY.getBoolean() && clientAPI.playerAccepts(FTBUFinals.MOD_ID, DisplayType.Polygon))
            {
                int dim = 0;

                MapPolygon poly = PolygonHelper.createChunkPolygon(pos.chunkXPos, 100, pos.chunkZPos);
                ShapeProperties shapeProperties = new ShapeProperties();

                shapeProperties.setFillOpacity(0.3F);
                shapeProperties.setStrokeOpacity(0.2F);

                StringBuilder sb = new StringBuilder();

                if(chunk.team != null)
                {
                    shapeProperties.setFillColor(chunk.team.color.getColor());

                    sb.append(chunk.team.formattedName);

                    sb.append('\n');
                    sb.append(TextFormatting.GREEN);
                    sb.append(FTBULang.CHUNKTYPE_CLAIMED.translate());
                }
                else
                {
                    shapeProperties.setFillColor(0x000000);
                }

                shapeProperties.setStrokeColor(0x000000);

                PolygonOverlay chunkOverlay = new PolygonOverlay(FTBUFinals.MOD_ID, "claimed_" + dim + '_' + pos.chunkXPos + '_' + pos.chunkZPos, dim, shapeProperties, poly);
                chunkOverlay.setOverlayGroupName("Claimed Chunks").setTitle(sb.toString());
                polygons.put(posLong, chunkOverlay);
                clientAPI.show(chunkOverlay);
            }
            else
            {
                PolygonOverlay p = polygons.get(posLong);

                if(p != null)
                {
                    clientAPI.remove(p);
                    polygons.remove(posLong);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}