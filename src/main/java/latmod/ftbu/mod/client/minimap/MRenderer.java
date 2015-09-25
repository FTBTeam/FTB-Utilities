package latmod.ftbu.mod.client.minimap;

import org.lwjgl.opengl.GL11;

import latmod.core.util.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.ChunkType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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
	
	public int renderX, renderY, size, tiles, startX, startY;
	public float zLevel;
	public boolean renderClaims, renderGrid, renderPlayers, renderAreaTitle;
	
	public MRenderer()
	{
		renderClaims = true;
		renderGrid = true;
		renderPlayers = true;
		renderAreaTitle = false;
	}
	
	@SuppressWarnings("unchecked")
	public void render()
	{
		int dimension = LatCoreMCClient.mc.thePlayer.dimension;
		
		Minimap m = Minimap.get(dimension);
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
			GuiLM.drawTexturedRectD(dx, dy, zLevel, tsize, tsize, ux, uy, ux + chunkD, uy + chunkD);
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
				
				GL11.glVertex3d(dx, dy, zLevel);
				GL11.glVertex3d(dx + tsize, dy, zLevel);
				
				GL11.glVertex3d(dx, dy, zLevel);
				GL11.glVertex3d(dx, dy + tsize, zLevel);
			}
			
			GL11.glVertex3d(renderX + size, renderY, zLevel);
			GL11.glVertex3d(renderX + size, renderY + size, zLevel);
			
			GL11.glVertex3d(renderX, renderY + size, zLevel);
			GL11.glVertex3d(renderX + size, renderY + size, zLevel);
			
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
		
		if(renderPlayers && !LatCoreMCClient.mc.theWorld.playerEntities.isEmpty())
		{
			FastList<EntityPlayer> list = new FastList<EntityPlayer>();
			list.addAll(LatCoreMCClient.mc.theWorld.playerEntities);
			
			GL11.glColor4f(1F, 1F, 1F, 0.7F);
			
			for(int i = 0; i < list.size(); i++)
			{
				EntityPlayer ep = list.get(i);
				if(ep.dimension == dimension && !ep.isInvisible() && !ep.isSneaking())
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
			int cx = MathHelperLM.chunk(LatCoreMCClient.mc.thePlayer.posX);
			int cy = MathHelperLM.chunk(LatCoreMCClient.mc.thePlayer.posZ);
			ChunkType t = m.getChunkType(cx, cy);
			String s = t.isClaimed() ? String.valueOf(m.getChunk(cx, cy).owner) : t.getIDS();
			LatCoreMCClient.mc.fontRenderer.drawString(s, renderX, renderY + size + 3, LMColorUtils.getColorFrom(t.chatColor));
		}
		
		LatCoreMCClient.popMaxBrightness();
		GL11.glPopAttrib();
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
}