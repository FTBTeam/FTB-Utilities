package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.*;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.claims.ChunkType;
import latmod.ftbu.mod.client.FTBURenderHandler;
import latmod.ftbu.mod.client.minimap.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiMinimap extends GuiLM implements IClientActionGui
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/minimap.png");
	public static final ResourceLocation tex_area = FTBU.mod.getLocation("textures/gui/minimap_area.png");
	public static final TextureCoords tex_mouse = new TextureCoords(tex, 156, 0, 32, 32);
	//public static final TextureCoords tex_pixel = new TextureCoords(tex, 140, 0, 1, 1);
	
	public static final TextureCoords[][][][] tex_area_coords = new TextureCoords[2][2][2][2];
	
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
	
	private static final TextureCoords getAreaCoords(int i)
	{ return new TextureCoords(tex_area, (i % 4) * 64, (i / 4) * 64, 64, 64); }
	
	public static final byte SIZE_CHUNKS = 9;
	public static final int SIZE = SIZE_CHUNKS * 16;
	private static int GL_ID = -1;
	public static boolean shouldRedraw;
	
	public final LMPlayer owner;
	public final int startX, startZ;
	public final int dimension;
	
	public final ButtonLM buttonRefresh, buttonClose;
	public final MapButton mapButton;
	
	public GuiMinimap()
	{
		super(new ContainerEmpty.ClientGui(), tex);
		xSize = 156;
		ySize = 185;
		shouldRedraw = false;
		
		owner = LMPlayer.getPlayer(container.player);
		startX = MathHelperLM.chunk(mc.thePlayer.posX) - 4;
		startZ = MathHelperLM.chunk(mc.thePlayer.posZ) - 4;
		dimension = mc.theWorld.provider.dimensionId;
		
		if(GL_ID == -1) GL_ID = GL11.glGenLists(1);
		
		final String loading = "Loading...";
		
		buttonRefresh = new ButtonLM(this, 6, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				mapButton.title = loading;
				Minimap.startThread(new ThreadMinimap(mc.theWorld, startX, startZ, SIZE_CHUNKS, owner));
				gui.playClickSound();
			}
		};
		
		buttonRefresh.title = FTBULang.button_refresh;
		
		buttonClose = new ButtonLM(this, 132, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				mc.displayGuiScreen(new GuiFriends());
			}
		};
		
		buttonClose.title = FTBULang.button_close;
		
		mapButton = new MapButton(this, 6, 26);
		mapButton.title = loading;
		
		buttonRefresh.onButtonPressed(0);
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		l.add(mapButton);
		l.add(buttonRefresh);
		l.add(buttonClose);
	}
	
	@SuppressWarnings("unchecked")
	public void drawBackground()
	{
		super.drawBackground();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		setTexture(tex);
		
		int bx = startX * 16;
		int bz = startZ * 16;
		
		mapButton.renderX = guiLeft + mapButton.posX;
		mapButton.renderY = guiTop + mapButton.posY;
		
		if(shouldRedraw)
		{
			shouldRedraw = false;
			
			GL11.glNewList(GL_ID, GL11.GL_COMPILE);
			Minimap m = Minimap.get(dimension);
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			GL11.glColor4f(0F, 0F, 0F, 1F);
			GuiLM.drawTexturedRectD(mapButton.renderX, mapButton.renderY, zLevel, SIZE, SIZE, 0D, 0D, 0D, 0D);
			
			for(int z = 0; z < SIZE_CHUNKS; z++)
			for(int x = 0; x < SIZE_CHUNKS; x++)
			{
				int pixels[] = m.getChunkPixels(startX + x, startZ + z);
				for(int i = 0; i < pixels.length; i++)
				{
					if(pixels[i] != 0)
					{
						LatCore.Colors.setGLColor(pixels[i], 255);
						GuiLM.drawTexturedRectD(mapButton.renderX + x * 16 + (i % 16), mapButton.renderY + z * 16 + (i / 16), zLevel, 1, 1, 0D, 0D, 0D, 0D);
					}
				}
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			setTexture(tex_area);
			
			for(int z = 0; z < SIZE_CHUNKS; z++) for(int x = 0; x < SIZE_CHUNKS; x++)
			{
				ChunkType t = m.getChunkType(startX + x, startZ + z);
				
				if(t.drawGrid())
				{
					boolean a = m.getChunkType(startX + x, startZ + z - 1) == t;
					boolean b = m.getChunkType(startX + x + 1, startZ + z) == t;
					boolean c = m.getChunkType(startX + x, startZ + z + 1) == t;
					boolean d = m.getChunkType(startX + x - 1, startZ + z) == t;
					
					TextureCoords tc = tex_area_coords[a ? 1 : 0][b ? 1 : 0][c ? 1 : 0][d ? 1 : 0];
					
					LatCore.Colors.setGLColor(t.areaColor, 255);
					GuiLM.drawTexturedRectD(mapButton.renderX + x * 16, mapButton.renderY + z * 16, zLevel, 16, 16, tc.minU, tc.minV, tc.maxU, tc.maxV);
				}
			}
			
			GL11.glEndList();
			refreshWidgets();
		}
		
		GL11.glCallList(GL_ID);
		
		if(Waypoints.hasWaypoints())
		{
			setTexture(FTBURenderHandler.texMarker);
			
			for(Waypoint w : Waypoints.getAll())
			{
				if(w.dim == dimension && w.enabled && w.posX >= bx && w.posZ >= bz && w.posX < bx + SIZE_CHUNKS * 16 && w.posZ < bz + SIZE_CHUNKS * 16)
				{
					GL11.glColor4f(w.colR / 255F, w.colG / 255F, w.colB / 255F, 1F);
					GuiLM.drawTexturedRectD(mapButton.renderX + w.posX - bx, mapButton.renderY + w.posZ - bz, zLevel, 8, 8, 0D, 0D, 1D, 1D);
				}
			}
		}
		
		{
			FastList<EntityPlayer> list = new FastList<EntityPlayer>();
			list.addAll(mc.theWorld.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(startX * 16, Double.NEGATIVE_INFINITY, startZ * 16, (startX + SIZE_CHUNKS) * 16, Double.POSITIVE_INFINITY, (startZ + SIZE_CHUNKS) * 16)));
			list.add(mc.thePlayer);
			
			if(!list.isEmpty())
			{
				GL11.glColor4f(1F, 1F, 1F, 0.7F);
				
				for(EntityPlayer ep : list)
				{
					if(!ep.isInvisible())
						GuiLM.drawPlayerHead(ep.getCommandSenderName(), mapButton.renderX + ep.posX - bx - 4, mapButton.renderY + ep.posZ - bz - 4, 8, 8, zLevel);
				}
			}
		}
		
		if(mapButton.mouseOver())
		{
			GL11.glColor4f(0.1F, 1F, 0.7F, 0.8F);
			tex_mouse.render(this, mapButton.renderX + (mapButton.chunkX() - startX) * 16 - guiLeft, mapButton.renderY + (mapButton.chunkZ() - startZ) * 16 - guiTop, 16, 16);
		}
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		buttonRefresh.render(Icons.map);
		buttonClose.render(Icons.accept);
	}
	
	public void onGuiClosed()
	{
		Minimap.stopThread();
	}
	
	public void onClientAction(String action)
	{
		shouldRedraw = true;
	}
	
	public static class MapButton extends ButtonLM
	{
		public final GuiMinimap guiM;
		public int renderX, renderY;
		
		public MapButton(GuiMinimap g, int x, int y)
		{
			super(g, x, y, SIZE, SIZE);
			guiM = g;
		}
		
		public int chunkX()
		{ return guiM.startX + (gui.mouseXR - posX) / 16; }
		
		public int chunkZ()
		{ return guiM.startZ + (gui.mouseYR - posY) / 16; }
		
		public void onButtonPressed(int b)
		{
			MessageLM.NET.sendToServer(new MessageClaimChunk(guiM.dimension, chunkX(), chunkZ(), (b == 0) ? 1 : 0));
			gui.playClickSound();
		}
		
		public void addMouseOverText(FastList<String> l)
		{
			ChunkType.getMessage(guiM.dimension, chunkX(), chunkZ(), guiM.owner, l, isShiftKeyDown());
		}
	}
}