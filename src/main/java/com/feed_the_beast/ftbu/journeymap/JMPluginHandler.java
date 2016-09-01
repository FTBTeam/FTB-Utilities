package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.client.CachedClientData;
import com.latmod.lib.math.ChunkDimPos;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 07.02.2016.
 */
@SideOnly(Side.CLIENT)
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
    public void chunkChanged(ChunkDimPos pos, @Nullable CachedClientData.ChunkData chunk)
    {
        try
        {
            if(clientAPI.playerAccepts(FTBUFinals.MOD_ID, DisplayType.Polygon))
            {
                if(chunk != null)
                {
                    MapPolygon poly = PolygonHelper.createChunkPolygon(pos.posX, 100, pos.posZ);
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

                    PolygonOverlay chunkOverlay = new PolygonOverlay(FTBUFinals.MOD_ID, "claimed_" + pos.dim + '_' + pos.posX + '_' + pos.posZ, pos.dim, shapeProperties, poly);
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