package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBURenderHandler;
import latmod.ftbu.mod.player.ChunkType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class MRenderer
{
	public static final ResourceLocation tex_area = FTBU.mod.getLocation("textures/gui/minimap_area.png");
	public static final ResourceLocation tex_map_entity = FTBU.mod.getLocation("textures/gui/map_entity.png");
	public static final TextureCoords[][][][] tex_area_coords = new TextureCoords[2][2][2][2];
	
	private static final TextureCoords getAreaCoords(int i)
	{ return new TextureCoords(tex_area, (i % 4) * 64, (i / 4) * 64, 64, 64); }
	
	static
	{
		tex_area_coords[0][0][0][0] = getAreaCoords(0);
		tex_area_coords[1][1][1][1] = getAreaCoords(1);
		tex_area_coords[1][0][1][0] = getAreaCoords(2);
		tex_area_coords[0][1][0][1] = getAreaCoords(3);
		tex_area_coords[1][0][0][0] = getAreaCoords(4);
		tex_area_coords[0][1][0][0] = getAreaCoords(5);
		tex_area_coords[0][0][1][0] = getAreaCoords(6);
		tex_area_coords[0][0][0][1] = getAreaCoords(7);
		tex_area_coords[1][1][0][0] = getAreaCoords(8);
		tex_area_coords[0][1][1][0] = getAreaCoords(9);
		tex_area_coords[0][0][1][1] = getAreaCoords(10);
		tex_area_coords[1][0][0][1] = getAreaCoords(11);
		tex_area_coords[0][1][1][1] = getAreaCoords(12);
		tex_area_coords[1][0][1][1] = getAreaCoords(13);
		tex_area_coords[1][1][0][1] = getAreaCoords(14);
		tex_area_coords[1][1][1][0] = getAreaCoords(15);
	}
	
	public Minecraft mc;
	public int renderX, renderY, size, tiles, startX, startY;
	public float zLevel;
	public boolean renderClaims, renderGrid, renderPlayers, renderWaypoints, renderAreaTitle;
	
	public MRenderer()
	{
		mc = LatCoreMCClient.getMinecraft();
		renderClaims = true;
		renderGrid = true;
		renderPlayers = true;
		renderWaypoints = true;
		renderAreaTitle = false;
	}
	
	@SuppressWarnings("unchecked")
	public void render()
	{
		Minimap m = Minimap.get(mc.thePlayer.dimension);
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		LatCoreMCClient.pushMaxBrightness();
		
		double tilesD = 1D / (double)tiles;
		double tsize = size * tilesD;
		double chunkD = 1D / (double)MArea.size_c;
		
		GL11.glColor4f(1F, 1F, 1F, 0.9F);
		
		for(int y = 0; y < tiles; y++)
		for(int x = 0; x < tiles; x++)
		{
			MChunk c = m.loadChunk(x + startX, y + startY);
			
			double ux = c.rposX * chunkD;
			double uy = c.rposY * chunkD;
			
			double dx = renderX + x * tsize;
			double dy = renderY + y * tsize;
			
			c.area.setTexture();
			GuiLM.drawTexturedRectD(dx, dy, 0D, tsize, tsize, ux, uy, ux + chunkD, uy + chunkD);
		}
		
		if(renderGrid)
		{
			GL11.glLineWidth(0.5F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(0.7F, 0.7F, 0.7F, 0.5F);
			GL11.glBegin(GL11.GL_LINES);
			
			for(int y = 0; y < tiles; y++)
			for(int x = 0; x < tiles; x++)
			{
				double dx = renderX + x * tsize;
				double dy = renderY + y * tsize;
				
				GL11.glVertex2d(dx, dy);
				GL11.glVertex2d(dx + tsize, dy);
				
				GL11.glVertex2d(dx, dy);
				GL11.glVertex2d(dx, dy + tsize);
			}
			
			GL11.glVertex2d(renderX + size, renderY);
			GL11.glVertex2d(renderX + size, renderY + size);
			
			GL11.glVertex2d(renderX, renderY + size);
			GL11.glVertex2d(renderX + size, renderY + size);
			
			GL11.glEnd();
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glLineWidth(1F);
		}
		
		if(renderClaims)
		{
			LatCoreMCClient.setTexture(tex_area);
			
			for(int y = 0; y < tiles; y++)
			for(int x = 0; x < tiles; x++)
			{
				int cx = x + startX;
				int cy = y + startY;
				MChunk ch = m.loadChunk(cx, cy);
				
				if(ch.type != null && ch.type.drawGrid())
				{
					boolean a = m.getChunkType(cx, cy - 1) == ch.type;
					boolean b = m.getChunkType(cx + 1, cy) == ch.type;
					boolean c = m.getChunkType(cx, cy + 1) == ch.type;
					boolean d = m.getChunkType(cx - 1, cy) == ch.type;
					
					TextureCoords tc = tex_area_coords[a ? 1 : 0][b ? 1 : 0][c ? 1 : 0][d ? 1 : 0];
					
					LMColorUtils.setGLColor(ch.type.areaColor, 255);
					GuiLM.drawTexturedRectD(renderX + x * tsize, renderY + y * tsize, zLevel, tsize, tsize, tc.minU, tc.minV, tc.maxU, tc.maxV);
				}
			}
		}
		
		if(renderWaypoints && Waypoints.hasWaypoints())
		{
			LatCoreMCClient.setTexture(FTBURenderHandler.texMarker);
			
			for(Waypoint w : Waypoints.getAll())
			{
				if(w.enabled && w.dim == mc.thePlayer.dimension)
				{
					GL11.glColor4f(w.colR / 255F, w.colG / 255F, w.colB / 255F, 1F);
					
					double x = renderX + ((MathHelperLM.chunk(w.posX) - startX) * 16D + MathHelperLM.wrap(w.posX, 16D)) * tsize / 16D;
					double y = renderY + ((MathHelperLM.chunk(w.posZ) - startY) * 16D + MathHelperLM.wrap(w.posZ, 16D)) * tsize / 16D;
					
					if(x < renderX) x = renderX;
					if(y < renderY) y = renderY;
					if(x > renderX + size) x = renderX + size;
					if(y > renderY + size) y = renderY + size;
					
					GuiLM.drawTexturedRectD(x - 3.5D, y - 3.5D, zLevel, 8, 8, 0D, 0D, 1D, 1D);
				}
			}
		}
		
		if(renderPlayers && !mc.theWorld.playerEntities.isEmpty())
		{
			FastList<EntityPlayer> list = new FastList<EntityPlayer>();
			list.addAll(mc.theWorld.playerEntities);
			
			GL11.glColor4f(1F, 1F, 1F, 0.7F);
			
			for(int i = 0; i < list.size(); i++)
			{
				EntityPlayer ep = list.get(i);
				if(ep.dimension == mc.thePlayer.dimension && !ep.isInvisible())
				{
					int cx = MathHelperLM.chunk(ep.posX);
					int cy = MathHelperLM.chunk(ep.posZ);
					
					if(cx >= startX && cy >= startY && cx < startX + tiles && cy < startY + tiles)
					{
						double x = ((cx - startX) * 16D + MathHelperLM.wrap(ep.posX, 16D)) * tsize / 16D;
						double y = ((cy - startY) * 16D + MathHelperLM.wrap(ep.posZ, 16D)) * tsize / 16D;
						
						GL11.glPushMatrix();
						GL11.glTranslated(renderX + x, renderY + y, 0D);
						GL11.glPushMatrix();
						//GL11.glRotatef((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
						GL11.glRotatef(ep.rotationYaw + 180F, 0F, 0F, 1F);
						LatCoreMCClient.setTexture(tex_map_entity);
						GuiLM.drawTexturedRectD(-8, -8, zLevel, 16, 16, 0D, 0D, 1D, 1D);
						GL11.glPopMatrix();
						GuiLM.drawPlayerHead(ep.getCommandSenderName(), -2, -2, 4, 4, zLevel);
						GL11.glPopMatrix();
					}
				}
			}
		}
		
		if(renderAreaTitle)
		{
			int cx = MathHelperLM.chunk(mc.thePlayer.posX);
			int cy = MathHelperLM.chunk(mc.thePlayer.posZ);
			ChunkType t = m.getChunkType(cx, cy);
			mc.fontRenderer.drawString(t.getIDS(), renderX, renderY + size + 3, LMColorUtils.getColorFrom(t.chatColor));
		}
		
		LatCoreMCClient.popMaxBrightness();
		GL11.glPopAttrib();
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
}