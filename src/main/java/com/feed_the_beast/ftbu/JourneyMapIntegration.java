package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.util.LMColorUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.gui.ClaimedChunks;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 17.01.2016.
 */
@ClientPlugin
public class JourneyMapIntegration implements IClientPlugin
{
    public static JourneyMapIntegration INST;
    private IClientAPI clientAPI;
    private Map<ChunkPos, PolygonOverlay> polygons = new HashMap<>();

    @Override
    public void initialize(IClientAPI api)
    {
        INST = this;
        clientAPI = api;
        FTBUClient.HAS_JM = true;
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
                clearData();
                break;
        }
    }

    public void clearData()
    {
        if(!polygons.isEmpty())
        {
            polygons.clear();
            clientAPI.removeAll(FTBUFinals.MOD_ID);
        }
    }

    public void chunkChanged(ChunkPos pos, ClaimedChunks.Data chunk)
    {
        if(!polygons.isEmpty() && (!FTBUClientConfig.JOURNEYMAP_OVERLAY.getBoolean() || !clientAPI.playerAccepts(FTBUFinals.MOD_ID, DisplayType.Polygon)))
        {
            clearData();
            return;
        }

        try
        {
            if(chunk.isClaimed())
            {
                int dim = 0;

                MapPolygon poly = PolygonHelper.createChunkPolygon(pos.chunkXPos, 100, pos.chunkZPos);
                ShapeProperties shapeProperties = new ShapeProperties();

                shapeProperties.setFillOpacity(0.3F);
                shapeProperties.setStrokeOpacity(0.2F);

                StringBuilder sb = new StringBuilder();

                shapeProperties.setFillColor(0x00FFFFFF | LMColorUtils.getColorFromID(chunk.team.colorID));
                sb.append(chunk.team.formattedName);

                sb.append('\n');
                sb.append(TextFormatting.GREEN);
                sb.append(FTBULang.CHUNKTYPE_CLAIMED.translate());

                shapeProperties.setStrokeColor(0x000000);

                PolygonOverlay chunkOverlay = new PolygonOverlay(FTBUFinals.MOD_ID, "claimed_" + dim + '_' + pos.chunkXPos + '_' + pos.chunkZPos, dim, shapeProperties, poly);
                chunkOverlay.setOverlayGroupName("Claimed Chunks").setTitle(sb.toString());
                polygons.put(pos, chunkOverlay);
                clientAPI.show(chunkOverlay);
            }
            else
            {
                PolygonOverlay p = polygons.get(pos);

                if(p != null)
                {
                    clientAPI.remove(p);
                    polygons.remove(pos);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}