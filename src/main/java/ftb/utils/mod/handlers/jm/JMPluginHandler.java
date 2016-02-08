package ftb.utils.mod.handlers.jm;

import ftb.utils.mod.FTBUFinals;
import ftb.utils.mod.client.gui.claims.ClaimedAreasClient;
import ftb.utils.world.LMWorldClient;
import ftb.utils.world.claims.ClaimedChunks;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.*;
import journeymap.client.api.model.*;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.*;

/**
 * Created by LatvianModder on 07.02.2016.
 */
public class JMPluginHandler implements IJMPluginHandler
{
	public final IClientAPI clientAPI;
	public final List<Displayable> displayables;
	
	public JMPluginHandler(IClientAPI api)
	{
		clientAPI = api;
		displayables = new ArrayList<>();
	}
	
	public void refresh(int dim)
	{
		clientAPI.removeAll(FTBUFinals.MOD_ID);
		
		if(clientAPI.playerAccepts(FTBUFinals.MOD_ID, DisplayType.Polygon))
		{
			Map<ChunkCoordIntPair, Integer> chunkTypes = ClaimedAreasClient.getChunkTypes();
			
			if(!chunkTypes.isEmpty())
			{
				for(Map.Entry<ChunkCoordIntPair, Integer> e : chunkTypes.entrySet())
					set(e.getKey(), e.getValue().intValue(), dim);
			}
		}
	}
	
	public void set(ChunkCoordIntPair pos, int id, int dim)
	{
		try
		{
			int color = 0xFF000000 | ClaimedChunks.getChunkTypeFromI(id).getAreaColor(LMWorldClient.inst.clientPlayer);
			
			ShapeProperties shapeProps = new ShapeProperties().setStrokeWidth(2.0F).setStrokeColor(color).setStrokeOpacity(0.7F).setFillColor(color).setFillOpacity(0.4F);
			MapPolygon polygon = PolygonHelper.createChunkPolygon(pos.chunkXPos, 70, pos.chunkZPos);
			PolygonOverlay chunkOverlay = new PolygonOverlay(FTBUFinals.MOD_ID, "claimed_" + pos, dim, shapeProps, polygon);
			chunkOverlay.setOverlayGroupName("Claimed Chunks").setTitle("Claimed Chunk");
			clientAPI.show(chunkOverlay);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}