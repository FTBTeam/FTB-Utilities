package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldClient;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBURenderHandler;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.client.minimap.*;
import latmod.ftbu.mod.player.ChunkType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiMinimap extends GuiLM implements IClientActionGui
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/minimap.png");
	public static final ResourceLocation tex_area = FTBU.mod.getLocation("textures/gui/minimap_area.png");
	public static final TextureCoords tex_mouse = new TextureCoords(tex, 156, 0, 32, 32);
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
	
	public static final int CHUNKS_OFFSET = 4;
	public static final int SIZE_CHUNKS = CHUNKS_OFFSET * 2 + 1;
	public static final int SIZE = SIZE_CHUNKS * 16;
	
	public final int startX, startZ;
	public final int dimension;
	private static int GL_ID = -1;
	public static boolean shouldRedraw = true;
	
	public final ButtonLM buttonRefresh, buttonClose;
	public final ItemButtonLM buttonSafe;
	public final MapButton mapButton;
	
	public GuiMinimap()
	{
		super(null, tex);
		xSize = 156;
		ySize = 185;
		hideNEI = true;
		
		if(GL_ID == -1) GL_ID = GL11.glGenLists(1);
		
		startX = MathHelperLM.chunk(mc.thePlayer.posX) - CHUNKS_OFFSET;
		startZ = MathHelperLM.chunk(mc.thePlayer.posZ) - CHUNKS_OFFSET;
		dimension = mc.theWorld.provider.dimensionId;
		
		final String loading = "Loading...";
		
		buttonRefresh = new ButtonLM(this, 6, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				mapButton.title = loading;
				LMNetHelper.sendToServer(new MessageAreaRequest(startX - 1, startZ - 1, dimension, SIZE_CHUNKS + 2));
				Minimap.startThread(new ThreadMinimap(mc.theWorld, startX, startZ, SIZE_CHUNKS));
				gui.playClickSound();
			}
		};
		
		buttonRefresh.title = FTBULang.button_refresh;
		
		buttonClose = new ButtonLM(this, 132, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				mc.displayGuiScreen(new GuiFriends(null));
			}
		};
		
		buttonClose.title = FTBULang.button_close;
		
		buttonSafe = new ItemButtonLM(this, 28, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				LMNetHelper.sendToServer(new MessageClientGuiAction(MessageClientGuiAction.ACTION_SET_SAFE_CHUNKS, LMWorldClient.inst.clientPlayer.claimSettings.isSafe() ? 0 : 1));
			}
		};
		
		buttonSafe.setItem(new ItemStack(Items.skull, 1, 4));
		
		mapButton = new MapButton(this, 6, 26);
		mapButton.title = loading;
		
		buttonRefresh.onButtonPressed(0);
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		l.add(mapButton);
		l.add(buttonRefresh);
		l.add(buttonClose);
		l.add(buttonSafe);
	}
	
	@SuppressWarnings("unchecked")
	public void drawBackground()
	{
		super.drawBackground();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		setTexture(tex);
		
		int renderX = guiLeft + mapButton.posX;
		int renderY = guiTop + mapButton.posY;
		
		int bx = startX * 16;
		int bz = startZ * 16;
		
		GL11.glEnable(GL11.GL_BLEND);
		
		if(shouldRedraw || (dimension != mc.theWorld.provider.dimensionId))
		{
			shouldRedraw = false;
			
			if(GL_ID == -1) GL_ID = GL11.glGenLists(1);
			
			Tessellator ts = Tessellator.instance;
			
			GL11.glNewList(GL_ID, GL11.GL_COMPILE);
			Minimap m = Minimap.get(dimension);
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			GL11.glColor4f(0F, 0F, 0F, 1F);
			
			ts.startDrawingQuads();
			
			ts.addTranslation(renderX, renderY, zLevel);
			ts.setColorRGBA(0, 0, 0, 255);
			ts.addVertex(0D, SIZE, 0D);
			ts.addVertex(SIZE, SIZE, 0D);
			ts.addVertex(SIZE, 0D, 0D);
			ts.addVertex(0D, 0D, 0D);
			
			//GuiLM.drawTexturedRectD(, renderY, zLevel, SIZE, SIZE, 0D, 0D, 0D, 0D);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			
			/*
			for(int z = 0; z < SIZE_CHUNKS; z++)
			for(int x = 0; x < SIZE_CHUNKS; x++)
			{
				int pixels[] = m.getChunkPixels(startX + x, startZ + z);
				for(int i = 0; i < pixels.length; i++)
				{
					if(pixels[i] != 0)
					{
						//LatCore.Colors.setGLColor(pixels[i], 255);
						//GuiLM.drawTexturedRectD(renderX + x * 16 + (i % 16), renderY + z * 16 + (i / 16), zLevel, 1, 1, 0D, 0D, 0D, 0D);
						double rx = x * 16 + (i % 16);
						double rz = z * 16 + (i / 16);
						
						ts.setColorOpaque_I(pixels[i]);
						ts.addVertex(rx, rz + 1D, 0D);
						ts.addVertex(rx + 1D, rz + 1D, 0D);
						ts.addVertex(rx + 1D, rz, 0D);
						ts.addVertex(rx, rz, 0D);
					}
				}
			}*/
			
			ts.addTranslation(-renderX, -renderY, -zLevel);
			ts.draw();
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			if(!isShiftKeyDown())
			{
				mc.getTextureManager().bindTexture(tex_area);
				
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
						
						LMColorUtils.setGLColor(t.areaColor, 255);
						GuiLM.drawTexturedRectD(renderX + x * 16, renderY + z * 16, zLevel, 16, 16, tc.minU, tc.minV, tc.maxU, tc.maxV);
					}
				}
			}
			
			GL11.glEndList();
		}
		
		GL11.glCallList(GL_ID);
		
		if(Minimap.renderWaypoints.getB() && Waypoints.hasWaypoints())
		{
			mc.getTextureManager().bindTexture(FTBURenderHandler.texMarker);
			
			for(Waypoint w : Waypoints.getAll())
			{
				if(w.enabled && w.dim == dimension)
				{
					GL11.glColor4f(w.colR / 255F, w.colG / 255F, w.colB / 255F, 1F);
					
					double x = renderX + (w.posX - bx);
					double y = renderY + (w.posZ - bz);
					
					if(x < renderX) x = renderX;
					if(y < renderY) y = renderY;
					if(x > renderX + SIZE) x = renderX + SIZE;
					if(y > renderY + SIZE) y = renderY + SIZE;
					
					GuiLM.drawTexturedRectD(x - 3.5D, y - 3.5D, zLevel, 8, 8, 0D, 0D, 1D, 1D);
				}
			}
		}
		
		if(Minimap.renderPlayers.getB())
		{
			FastList<EntityPlayer> list = new FastList<EntityPlayer>();
			list.addAll(mc.theWorld.playerEntities);
			//list.add(mc.thePlayer);
			
			if(!list.isEmpty())
			{
				GL11.glColor4f(1F, 1F, 1F, 0.7F);
				
				for(int i = 0; i < list.size(); i++)
				{
					EntityPlayer ep = list.get(i);
					if(ep.dimension == dimension && !ep.isInvisible() && ep.posX >= bx && ep.posZ >= bz && ep.posX < bx + SIZE && ep.posZ < bz + SIZE)
					{
						GL11.glPushMatrix();
						GL11.glTranslated(renderX + ep.posX - bx, renderY + ep.posZ - bz, 0D);
						//GL11.glRotatef(ep.rotationYaw + 180F, 0F, 0F, 1F);
						GuiLM.drawPlayerHead(ep.getCommandSenderName(), -4, -4, 8, 8, zLevel);
						GL11.glPopMatrix();
					}
				}
			}
		}
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		if(mapButton.mouseOver())
		{
			GL11.glColor4f(0.1F, 1F, 0.7F, 0.8F);
			tex_mouse.render(this, renderX + (mapButton.chunkX() - startX) * 16 - guiLeft, renderY + (mapButton.chunkZ() - startZ) * 16 - guiTop, 16, 16);
		}
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		buttonRefresh.render(Icons.map);
		buttonClose.render(Icons.accept);
		buttonSafe.render();
		
		if(LMWorldClient.inst.clientPlayer.claimSettings.isSafe())
		{
			zLevel = 500;
			GL11.glColor4f(1F, 1F, 1F, 0.75F);
			buttonSafe.render(Icons.close);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			zLevel = 0;
		}
	}
	
	public void drawText(FastList<String> l)
	{
		if(LMWorldClient.inst.clientPlayer == null) return;
		String s = LMWorldClient.inst.clientPlayer.claimedChunks + " / " + LMWorldClient.inst.clientPlayer.maxClaimPower;
		fontRendererObj.drawString(s, guiLeft + xSize - fontRendererObj.getStringWidth(s) - 4, guiTop + ySize - 12, 0xFFFFFFFF);
		super.drawText(l);
	}
	
	public void onLMGuiClosed()
	{
		Minimap.stopThread();
	}
	
	public void onClientDataChanged()
	{
		shouldRedraw = true;
	}
	
	public static class MapButton extends ButtonLM
	{
		public final GuiMinimap guiM;
		
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
			LMNetHelper.sendToServer(new MessageClaimChunk(guiM.dimension, chunkX(), chunkZ(), b == 0));
			gui.playClickSound();
		}
		
		public void addMouseOverText(FastList<String> l)
		{
			ChunkType.getMessage(guiM.dimension, chunkX(), chunkZ(), l, isShiftKeyDown());
		}
	}
}