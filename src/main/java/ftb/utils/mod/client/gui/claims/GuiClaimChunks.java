package ftb.utils.mod.client.gui.claims;

import ftb.lib.TextureCoords;
import ftb.lib.api.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.*;
import ftb.lib.api.gui.widgets.*;
import ftb.lib.mod.client.gui.friends.GuiFriends;
import ftb.lib.mod.net.MessageRequestSelfUpdate;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.client.FTBUClient;
import ftb.utils.net.*;
import ftb.utils.world.*;
import latmod.lib.MathHelperLM;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.fml.relauncher.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.*;

@SideOnly(Side.CLIENT)
public class GuiClaimChunks extends GuiLM implements GuiYesNoCallback // implements IClientActionGui
{
	public static final int tiles_tex = 16;
	public static final int tiles_gui = 15;
	public static final double UV = (double) tiles_gui / (double) tiles_tex;
	public static final ResourceLocation tex_area = new ResourceLocation("ftbl", "textures/world/minimap_area.png");
	public static final ResourceLocation tex_map_entity = new ResourceLocation("ftbl", "textures/world/entity.png");
	public static final TextureCoords[][][][] tex_area_coords = new TextureCoords[2][2][2][2];
	
	private static TextureCoords getAreaCoords(int i)
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
	
	public static int textureID = -1;
	public static ByteBuffer pixelBuffer = null;
	
	public final long adminToken;
	public final ForgePlayerSPSelf playerLM;
	public final int currentDim, startX, startY;
	
	public final ButtonLM buttonRefresh, buttonClose, buttonSettings, buttonUnclaimAll;
	public final MapButton mapButtons[];
	public final PanelLM panelButtons;
	
	public ThreadReloadArea thread = null;
	
	public GuiClaimChunks(long token)
	{
		super(null, null);
		mainPanel.width = mainPanel.height = tiles_gui * 16;
		
		adminToken = token;
		playerLM = ForgeWorldSP.inst.clientPlayer;
		startX = MathHelperLM.chunk(mc.thePlayer.posX) - (int) (tiles_gui * 0.5D);
		startY = MathHelperLM.chunk(mc.thePlayer.posZ) - (int) (tiles_gui * 0.5D);
		currentDim = FTBLibClient.getDim();
		
		buttonClose = new ButtonLM(this, 0, 0, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				FTBLibClient.playClickSound();
				FTBLibClient.openGui(new GuiFriends(null));
			}
		};
		
		buttonRefresh = new ButtonLM(this, 0, 16, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				thread = new ThreadReloadArea(mc.theWorld, GuiClaimChunks.this);
				thread.start();
				new MessageAreaRequest(startX, startY, tiles_gui, tiles_gui).sendToServer();
				new MessageRequestSelfUpdate().sendToServer();
				FTBLibClient.playClickSound();
			}
		};
		
		buttonRefresh.title = FTBLibLang.button_refresh();
		
		buttonSettings = new ButtonLM(this, 0, 32, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				FTBLibClient.playClickSound();
				new MessageButtonPressed(MessageButtonPressed.CLAIMED_CHUNKS_SETTINGS, 0).sendToServer();
			}
		};
		
		buttonSettings.title = FTBLibLang.button_settings();
		
		buttonUnclaimAll = new ButtonLM(this, 0, 48, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				FTBLibClient.playClickSound();
				String s = isShiftKeyDown() ? FTBU.mod.translate("button.claims_unclaim_all_q") : FTBU.mod.translate("button.claims_unclaim_all_dim_q", FTBLibClient.mc.theWorld.provider.getDimensionName());
				FTBLibClient.openGui(new GuiYesNo(GuiClaimChunks.this, s, "", isShiftKeyDown() ? 1 : 0));
			}
			
			public void addMouseOverText(List<String> l)
			{
				if(isShiftKeyDown()) l.add(FTBU.mod.translate("button.claims_unclaim_all"));
				else
					l.add(FTBU.mod.translate("button.claims_unclaim_all_dim", FTBLibClient.mc.theWorld.provider.getDimensionName()));
			}
		};
		
		panelButtons = new PanelLM(this, 0, 0, 16, 0)
		{
			public void addWidgets()
			{
				add(buttonClose);
				add(buttonRefresh);
				
				if(adminToken == 0L)
				{
					add(buttonSettings);
					add(buttonUnclaimAll);
				}
				
				height = widgets.size() * 16;
			}
			
			public int getAX()
			{ return gui.getGui().width - 16; }
			
			public int getAY()
			{ return 0; }
		};
		
		mapButtons = new MapButton[tiles_gui * tiles_gui];
		for(int i = 0; i < mapButtons.length; i++)
			mapButtons[i] = new MapButton(this, 0, 0, i);
	}
	
	public void initLMGui()
	{
		buttonRefresh.onButtonPressed(0);
	}
	
	public void addWidgets()
	{
		mainPanel.addAll(mapButtons);
		mainPanel.add(panelButtons);
	}
	
	public void drawBackground()
	{
		if(currentDim != FTBLibClient.getDim())
		{
			mc.thePlayer.closeScreen();
			return;
		}
		
		if(pixelBuffer != null)
		{
			if(textureID == -1)
			{
				textureID = TextureUtil.glGenTextures();
				new MessageAreaRequest(startX, startY, tiles_gui, tiles_gui).sendToServer();
			}
			
			//boolean hasBlur = false;
			//int filter = hasBlur ? GL11.GL_LINEAR : GL11.GL_NEAREST;
			int filter = GL11.GL_NEAREST;
			GlStateManager.bindTexture(textureID);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, tiles_tex * 16, tiles_tex * 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
			pixelBuffer = null;
			thread = null;
		}
		
		GlStateManager.color(0F, 0F, 0F, 1F);
		drawBlankRect(mainPanel.posX - 2, mainPanel.posY - 2, zLevel, mainPanel.width + 4, mainPanel.height + 4);
		//drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		if(textureID != -1 && thread == null)
		{
			GlStateManager.bindTexture(textureID);
			drawTexturedRectD(mainPanel.posX, mainPanel.posY, zLevel, tiles_gui * 16, tiles_gui * 16, 0D, 0D, UV, UV);
		}
		
		super.drawBackground();
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		//setTexture(tex);
		
		renderMinimap();
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		for(MapButton mapButton : mapButtons) mapButton.renderWidget();
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		buttonRefresh.render(GuiIcons.refresh);
		buttonClose.render(GuiIcons.accept);
		
		if(adminToken == 0L)
		{
			buttonSettings.render(GuiIcons.settings);
			buttonUnclaimAll.render(GuiIcons.remove);
		}
	}
	
	public void drawText(List<String> l)
	{
		FTBUPlayerDataSP d = FTBUPlayerDataSP.get(ForgeWorldSP.inst.clientPlayer);
		String s = FTBU.mod.translate("label.cchunks_count", (d.claimedChunks + " / " + d.maxClaimedChunks));
		fontRendererObj.drawString(s, width - fontRendererObj.getStringWidth(s) - 4, height - 12, 0xFFFFFFFF);
		s = FTBU.mod.translate("label.lchunks_count", (d.loadedChunks + " / " + d.maxLoadedChunks));
		fontRendererObj.drawString(s, width - fontRendererObj.getStringWidth(s) - 4, height - 24, 0xFFFFFFFF);
		
		super.drawText(l);
	}
	
	public void onLMGuiClosed()
	{
	}
	
	public static Map.Entry<TextureCoords, ChunkType> getForChunk(ChunkCoordIntPair pos)
	{
		ChunkType type = FTBUWorldDataSP.get().getType(pos);
		if(type.drawGrid())
		{
			boolean a = type.equals(FTBUWorldDataSP.get().getType(new ChunkCoordIntPair(pos.chunkXPos, pos.chunkZPos - 1)));
			boolean b = type.equals(FTBUWorldDataSP.get().getType(new ChunkCoordIntPair(pos.chunkXPos + 1, pos.chunkZPos)));
			boolean c = type.equals(FTBUWorldDataSP.get().getType(new ChunkCoordIntPair(pos.chunkXPos, pos.chunkZPos + 1)));
			boolean d = type.equals(FTBUWorldDataSP.get().getType(new ChunkCoordIntPair(pos.chunkXPos - 1, pos.chunkZPos)));
			return new AbstractMap.SimpleEntry<>(tex_area_coords[a ? 1 : 0][b ? 1 : 0][c ? 1 : 0][d ? 1 : 0], type);
		}
		
		return null;
	}
	
	public void renderMinimap()
	{
		FTBLibClient.setTexture(tex_area);
		
		for(int y = 0; y < tiles_gui; y++)
		{
			for(int x = 0; x < tiles_gui; x++)
			{
				int cx = x + startX;
				int cy = y + startY;
				
				Map.Entry<TextureCoords, ChunkType> type = getForChunk(new ChunkCoordIntPair(cx, cy));
				
				if(type != null)
				{
					TextureCoords tc = type.getKey();
					FTBLibClient.setGLColor(type.getValue().getAreaColor(playerLM), 255);
					GuiLM.drawTexturedRectD(mainPanel.posX + x * 16, mainPanel.posY + y * 16, zLevel, 16, 16, tc.minU, tc.minV, tc.maxU, tc.maxV);
				}
			}
		}
		
		if(!FTBLibClient.mc.theWorld.playerEntities.isEmpty())
		{
			ArrayList<EntityPlayer> list = new ArrayList<>();
			list.addAll(FTBLibClient.mc.theWorld.playerEntities);
			
			for(EntityPlayer ep : list)
			{
				if(ep.dimension == currentDim && !ep.isInvisible())
				{
					int cx = MathHelperLM.chunk(ep.posX);
					int cy = MathHelperLM.chunk(ep.posZ);
					
					if(cx >= startX && cy >= startY && cx < startX + tiles_gui && cy < startY + tiles_gui)
					{
						double x = ((cx - startX) * 16D + MathHelperLM.wrap(ep.posX, 16D));
						double y = ((cy - startY) * 16D + MathHelperLM.wrap(ep.posZ, 16D));
						
						GlStateManager.pushMatrix();
						GlStateManager.translate(mainPanel.posX + x, mainPanel.posY + y, 0D);
						GlStateManager.pushMatrix();
						//GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
						GlStateManager.rotate(ep.rotationYaw + 180F, 0F, 0F, 1F);
						FTBLibClient.setTexture(tex_map_entity);
						GlStateManager.color(1F, 1F, 1F, ep.isSneaking() ? 0.4F : 0.7F);
						GuiLM.drawTexturedRectD(-8, -8, zLevel, 16, 16, 0D, 0D, 1D, 1D);
						GlStateManager.popMatrix();
						GuiLM.drawPlayerHead(ep.getName(), -2, -2, 4, 4, zLevel);
						GlStateManager.popMatrix();
					}
				}
			}
			
			GlStateManager.color(1F, 1F, 1F, 1F);
		}
	}
	
	public void confirmClicked(boolean set, int id)
	{
		if(set && adminToken == 0L)
		{
			MessageClaimChunk msg = new MessageClaimChunk();
			msg.dim = GuiClaimChunks.this.currentDim;
			msg.token = GuiClaimChunks.this.adminToken;
			msg.pos = new ChunkCoordIntPair(0, 0);
			msg.type = (id == 1) ? MessageClaimChunk.ID_UNCLAIM_ALL_DIMS : MessageClaimChunk.ID_UNCLAIM_ALL;
			msg.sendToServer();
			new MessageAreaRequest(startX, startY, tiles_gui, tiles_gui).sendToServer();
		}
		
		FTBLibClient.openGui(this);
		refreshWidgets();
	}
	
	public static class MapButton extends ButtonLM
	{
		public final GuiClaimChunks gui;
		public final ChunkCoordIntPair chunk;
		
		public MapButton(GuiClaimChunks g, int x, int y, int i)
		{
			super(g, x, y, 16, 16);
			gui = g;
			posX += (i % tiles_gui) * width;
			posY += (i / tiles_gui) * height;
			chunk = new ChunkCoordIntPair(g.startX + (i % tiles_gui), g.startY + (i / tiles_gui));
		}
		
		public void onButtonPressed(int b)
		{
			if(gui.panelButtons.mouseOver()) return;
			if(gui.adminToken != 0L && b == 0) return;
			boolean ctrl = FTBUClient.loaded_chunks_space_key.get() ? Keyboard.isKeyDown(Keyboard.KEY_SPACE) : isCtrlKeyDown();
			
			MessageClaimChunk msg = new MessageClaimChunk();
			msg.dim = gui.currentDim;
			msg.token = gui.adminToken;
			msg.pos = chunk;
			msg.type = (b == 0) ? (ctrl ? MessageClaimChunk.ID_LOAD : MessageClaimChunk.ID_CLAIM) : (ctrl ? MessageClaimChunk.ID_UNLOAD : MessageClaimChunk.ID_UNCLAIM);
			msg.sendToServer();
			FTBLibClient.playClickSound();
		}
		
		public void addMouseOverText(List<String> l)
		{ FTBUWorldDataSP.get().getType(chunk).getMessage(l, isShiftKeyDown()); }
		
		public void renderWidget()
		{
			if(mouseOver())
			{
				GlStateManager.color(1F, 1F, 1F, 0.27F);
				drawBlankRect(getAX(), getAY(), gui.getZLevel(), 16, 16);
				GlStateManager.color(1F, 1F, 1F, 1F);
			}
		}
	}
}