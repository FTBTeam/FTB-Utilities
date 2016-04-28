package ftb.utils.journeymap;

import ftb.lib.TextureCoords;
import ftb.lib.api.ForgePlayer;
import ftb.lib.api.ForgeWorldSP;
import ftb.utils.FTBUFinals;
import ftb.utils.client.gui.claims.GuiClaimChunks;
import ftb.utils.world.ChunkType;
import ftb.utils.world.FTBUWorldDataSP;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.model.MapImage;
import latmod.lib.LMColorUtils;
import latmod.lib.MathHelperLM;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.Map;

/**
 * Created by LatvianModder on 07.02.2016.
 */
public class JMPluginHandler implements IJMPluginHandler
{
	public final IClientAPI clientAPI;
	
	public JMPluginHandler(IClientAPI api)
	{
		clientAPI = api;
	}
	
	@Override
	public void refresh(int dim)
	{
		clientAPI.removeAll(FTBUFinals.MOD_ID);
		
		if(FTBUWorldDataSP.get().isLoaded() && !FTBUWorldDataSP.get().chunks.isEmpty() && clientAPI.playerAccepts(FTBUFinals.MOD_ID, DisplayType.Polygon))
		{
			MapImage image;
			
			for(ChunkCoordIntPair pos : FTBUWorldDataSP.get().chunks.keySet())
			{
				Map.Entry<TextureCoords, ChunkType> e1 = GuiClaimChunks.getForChunk(pos);
				
				if(e1 != null)
				{
					try
					{
						TextureCoords tc = e1.getKey();
						ChunkType type = e1.getValue();
						
						int color = LMColorUtils.getRGBA(type.getAreaColor(ForgeWorldSP.inst.clientPlayer), 0);
						int x = MathHelperLM.unchunk(pos.chunkXPos);
						int z = MathHelperLM.unchunk(pos.chunkZPos);
						
						//GuiLM.drawTexturedRectD(mainPanel.posX + x * 16, mainPanel.posY + y * 16, zLevel, 16, 16, tc.minU, tc.minV, tc.maxU, tc.maxV);
						
						//						MapImage image = new MapImage(tc.texture, tc.posXI(), tc.posYI(), (int) tc.textureW, (int) tc.textureH, color, 1F);
						//						image.setDisplayWidth(tc.width);
						//						image.setDisplayHeight(tc.height);
						
						image = new MapImage(tc.texture, 16, 16);
						image.setColor(color);
						
						ImageOverlay chunkOverlay = new ImageOverlay(FTBUFinals.MOD_ID, "claimed_" + pos.chunkXPos + "_" + pos.chunkZPos, new BlockPos(x, 0, z), new BlockPos(x + 16, 0, z + 16), image);
						
						StringBuilder sb = new StringBuilder(type.langKey.format());
						
						if(type.asClaimed() != null)
						{
							sb.append('\n');
							sb.append(TextFormatting.GREEN.toString());
							
							ForgePlayer player = type.asClaimed().chunk.getOwner();
							sb.append((ForgeWorldSP.inst.clientPlayer.isFriend(player) ? TextFormatting.GREEN : TextFormatting.BLUE) + player.getProfile().getName());
							
							if(type.asClaimed().chunk.isChunkloaded)
							{
								sb.append('\n');
								sb.append(I18n.format("ftbu.chunktype.chunkloaded"));
							}
						}
						
						chunkOverlay.setOverlayGroupName("Claimed_Chunks").setTitle(sb.toString());
						clientAPI.show(chunkOverlay);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}
}