package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbl.api.ForgeTeam;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 07.02.2016.
 */
public class JMPluginHandler implements IJMPluginHandler
{
    private final IClientAPI clientAPI;
    private final Map<ChunkDimPos, PolygonOverlay> polygons;

    public JMPluginHandler(IClientAPI api)
    {
        clientAPI = api;
        polygons = new HashMap<>();
    }

    @Override
    public void mappingStarted()
    {
    }

    @Override
    public void mappingStopped()
    {
        polygons.clear();
        clientAPI.removeAll(FTBUFinals.MOD_ID);
    }

    @Override
    public void chunkChanged(ChunkDimPos pos, ClaimedChunk chunk)
    {
        try
        {
            if(FTBUWorldData.isLoadedW(ForgeWorldSP.inst) && clientAPI.playerAccepts(FTBUFinals.MOD_ID, DisplayType.Polygon))
            {
                if(chunk != null)
                {
                    ForgeTeam team = chunk.owner.getTeam();

                    MapPolygon poly = PolygonHelper.createChunkPolygon(pos.chunkXPos, 100, pos.chunkZPos);
                    ShapeProperties shapeProperties = new ShapeProperties();

                    shapeProperties.setFillOpacity(0.3F);
                    shapeProperties.setStrokeOpacity(0.2F);

                    StringBuilder sb = new StringBuilder();

                    if(team != null)
                    {
                        shapeProperties.setFillColor(team.getColor().color);

                        sb.append(team.getColor().textFormatting);
                        sb.append(team.getTitle());

                        sb.append('\n');
                        sb.append(TextFormatting.GREEN);
                        sb.append(ClaimedChunk.LANG_CLAIMED.translate());
                    }
                    else
                    {
                        shapeProperties.setFillColor(0x000000);
                    }

                    shapeProperties.setStrokeColor(0x000000);

                    PolygonOverlay chunkOverlay = new PolygonOverlay(FTBUFinals.MOD_ID, "claimed_" + pos.dim + '_' + pos.chunkXPos + '_' + pos.chunkZPos, pos.dim, shapeProperties, poly);
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
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}