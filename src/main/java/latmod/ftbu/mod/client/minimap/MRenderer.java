package latmod.ftbu.mod.client.minimap;

import org.lwjgl.opengl.GL11;

import ftb.lib.client.FTBLibClient;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.ChunkType;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class MRenderer
{
	public static final ResourceLocation tex_area = FTBU.mod.getLocation("textures/gui/minimap_area.png");
	public static final ResourceLocation tex_map_entity = FTBU.mod.getLocation("textures/gui/map_entity.png");
	public static final TextureCoords[][][][] tex_area_coords = new TextureCoords[2][2][2][2];
	private static final double chunkD = 1D / (double)MArea.size_c;
	
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
	
	public double renderX, renderY, tileSize;
	public int startX, startY, tilesX, tilesY;
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
		double sizeX = sizeX();
		double sizeY = sizeY();
		
		Minimap m = Minimap.get(LatCoreMCClient.getDim());
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		FTBLibClient.pushMaxBrightness();
		
		GL11.glColor4f(1F, 1F, 1F, 0.9F);
		
		for(int y = 0; y < tilesX; y++)
		for(int x = 0; x < tilesY; x++)
		{
			MChunk c = m.loadChunk(x + startX, y + startY);
			
			double ux = c.rposX * chunkD;
			double uy = c.rposY * chunkD;
			
			double dx = renderX + x * tileSize;
			double dy = renderY + y * tileSize;
			
			c.area.setTexture();
			GuiLM.drawTexturedRectD(dx, dy, zLevel, tileSize, tileSize, ux, uy, ux + chunkD, uy + chunkD);
		}
		
		if(renderGrid)
		{
			GL11.glLineWidth(0.5F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(0.7F, 0.7F, 0.7F, 0.5F);
			GL11.glBegin(GL11.GL_LINES);
			
			for(int y = 0; y < tilesX; y++)
			for(int x = 0; x < tilesY; x++)
			{
				double dx = renderX + x * tileSize;
				double dy = renderY + y * tileSize;
				
				GL11.glVertex3d(dx, dy, zLevel);
				GL11.glVertex3d(dx + tileSize, dy, zLevel);
				
				GL11.glVertex3d(dx, dy, zLevel);
				GL11.glVertex3d(dx, dy + tileSize, zLevel);
			}
			
			GL11.glVertex3d(renderX + sizeX, renderY, zLevel);
			GL11.glVertex3d(renderX + sizeX, renderY + sizeY, zLevel);
			
			GL11.glVertex3d(renderX, renderY + sizeY, zLevel);
			GL11.glVertex3d(renderX + sizeX, renderY + sizeY, zLevel);
			
			GL11.glEnd();
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glLineWidth(1F);
		}
		
		if(renderClaims)
		{
			FTBLibClient.setTexture(tex_area);
			
			for(int y = 0; y < tilesX; y++)
			for(int x = 0; x < tilesY; x++)
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
					
					FTBLibClient.setGLColor(ch.type.areaColor, 255);
					GuiLM.drawTexturedRectD(renderX + x * tileSize, renderY + y * tileSize, zLevel, tileSize, tileSize, tc.minU, tc.minV, tc.maxU, tc.maxV);
				}
			}
		}
		
		if(renderPlayers && !FTBLibClient.mc.theWorld.playerEntities.isEmpty())
		{
			FastList<EntityPlayer> list = new FastList<EntityPlayer>();
			list.addAll(FTBLibClient.mc.theWorld.playerEntities);
			
			for(int i = 0; i < list.size(); i++)
			{
				EntityPlayer ep = list.get(i);
				if(ep.dimension == m.dim && !ep.isInvisible())
				{
					int cx = MathHelperLM.chunk(ep.posX);
					int cy = MathHelperLM.chunk(ep.posZ);
					
					if(cx >= startX && cy >= startY && cx < startX + tilesX && cy < startY + tilesY)
					{
						double x = ((cx - startX) * 16D + MathHelperLM.wrap(ep.posX, 16D)) * tileSize / 16D;
						double y = ((cy - startY) * 16D + MathHelperLM.wrap(ep.posZ, 16D)) * tileSize / 16D;
						
						GL11.glPushMatrix();
						GL11.glTranslated(renderX + x, renderY + y, 0D);
						GL11.glPushMatrix();
						//GL11.glRotatef((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
						GL11.glRotatef(ep.rotationYaw + 180F, 0F, 0F, 1F);
						FTBLibClient.setTexture(tex_map_entity);
						GL11.glColor4f(1F, 1F, 1F, ep.isSneaking() ? 0.4F : 0.7F);
						GuiLM.drawTexturedRectD(-8, -8, zLevel, 16, 16, 0D, 0D, 1D, 1D);
						GL11.glPopMatrix();
						GuiLM.drawPlayerHead(ep.getCommandSenderName(), -2, -2, 4, 4, zLevel);
						GL11.glPopMatrix();
					}
				}
			}
			
			GL11.glColor4f(1F, 1F, 1F, 1F);
		}
		
		if(renderAreaTitle)
		{
			int cx = MathHelperLM.chunk(FTBLibClient.mc.thePlayer.posX);
			int cy = MathHelperLM.chunk(FTBLibClient.mc.thePlayer.posZ);
			ChunkType t = m.getChunkType(cx, cy);
			String s = t.isClaimed() ? String.valueOf(m.getChunk(cx, cy).owner) : t.getIDS();
			FTBLibClient.mc.fontRenderer.drawString(s, (int)renderX, (int)(renderY + sizeY + 3D), LMColorUtils.chatFormattingColors[t.chatColor.ordinal()]);
		}
		
		FTBLibClient.popMaxBrightness();
		GL11.glPopAttrib();
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}

	public double sizeX()
	{ return tileSize * tilesX; }
	
	public double sizeY()
	{ return tileSize * tilesY; }
}