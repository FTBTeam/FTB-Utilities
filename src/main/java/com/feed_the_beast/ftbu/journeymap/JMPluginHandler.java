package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.world.ChunkType;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import latmod.lib.MathHelperLM;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

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
    }

    @Override
    public void mappingStopped()
    {
        clientAPI.removeAll(FTBUFinals.MOD_ID);
        polygons.clear();
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
                    int x = MathHelperLM.unchunk(pos.chunkXPos);
                    int z = MathHelperLM.unchunk(pos.chunkZPos);

                    MapPolygon poly = new MapPolygon(new BlockPos(x, 0, z), new BlockPos(x + 16, 0, z), new BlockPos(x + 16, 0, z + 16), new BlockPos(x, 0, z + 16));
                    PolygonOverlay chunkOverlay = new PolygonOverlay(FTBUFinals.MOD_ID, "claimed_" + pos.dim + '_' + pos.chunkXPos + '_' + pos.chunkZPos, pos.dim, new ShapeProperties().setFillColor(type.getAreaColor(ForgeWorldSP.inst.clientPlayer)).setFillOpacity(1F), poly);

                    StringBuilder sb = new StringBuilder(type.getChatColor(ForgeWorldSP.inst.clientPlayer) + type.langKey.translate());

                    if(type.asClaimed() != null)
                    {
                        sb.append('\n');
                        sb.append(TextFormatting.GREEN.toString());

                        ForgePlayer player = type.asClaimed().chunk.getOwner();
                        sb.append(ForgeWorldSP.inst.clientPlayer.isFriend(player) ? TextFormatting.GREEN : TextFormatting.BLUE).append(player.getProfile().getName());

                        if(type.asClaimed().chunk.isChunkloaded)
                        {
                            sb.append('\n');
                            sb.append(TextFormatting.GOLD + I18n.translateToLocal("ftbu.chunktype.chunkloaded"));
                        }
                    }

                    chunkOverlay.setOverlayGroupName("Claimed_Chunks").setTitle(sb.toString());
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