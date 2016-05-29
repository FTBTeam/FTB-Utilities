package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeTeam;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.world.ChunkType;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.client.resources.I18n;
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
        polygons.clear();
        clientAPI.removeAll(FTBUFinals.MOD_ID);
    }

    @Override
    public void mappingStopped()
    {
        polygons.clear();
        clientAPI.removeAll(FTBUFinals.MOD_ID);
    }

    @Override
    public void chunkChanged(ChunkDimPos pos, ChunkType type)
    {
        try
        {
            if(FTBUWorldData.isLoadedW(ForgeWorldSP.inst) && clientAPI.playerAccepts(FTBUFinals.MOD_ID, DisplayType.Polygon))
            {
                if(type.drawGrid())
                {
                    MapPolygon poly = PolygonHelper.createChunkPolygon(pos.chunkXPos, 100, pos.chunkZPos);
                    ShapeProperties shapeProperties = new ShapeProperties();
                    shapeProperties.setFillColor(type.getAreaColor(ForgeWorldSP.inst.clientPlayer));
                    shapeProperties.setFillOpacity(0.3F);
                    shapeProperties.setStrokeOpacity(0.2F);
                    PolygonOverlay chunkOverlay = new PolygonOverlay(FTBUFinals.MOD_ID, "claimed_" + pos.dim + '_' + pos.chunkXPos + '_' + pos.chunkZPos, pos.dim, shapeProperties, poly);

                    StringBuilder sb = new StringBuilder(type.getChatColor(ForgeWorldSP.inst.clientPlayer) + type.langKey.translate());

                    if(type.asClaimed() != null)
                    {
                        sb.append('\n');
                        sb.append(TextFormatting.GREEN);

                        ForgePlayer owner = type.asClaimed().chunk.getOwner();

                        if(owner != null && owner.hasTeam())
                        {
                            ForgeTeam team = owner.getTeam();
                            sb.append(FTBLib.getFromDyeColor(team.getColor())).append(team.getTitle());

                            if(type.asClaimed().chunk.getFlag(ClaimedChunk.CHUNKLOADED))
                            {
                                sb.append('\n');
                                sb.append(TextFormatting.GOLD + I18n.format("ftbu.chunktype.chunkloaded"));
                            }
                        }
                    }

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