package latmod.ftbu.mod.client.gui.minimap;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.*;

import cpw.mods.fml.relauncher.*;
import ftb.lib.client.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.config.FTBUConfigClaims;
import latmod.ftbu.net.*;
import latmod.ftbu.util.LMSecurityLevel;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiMinimap extends GuiLM // implements IClientActionGui
{
	public static final int tiles = 16;
	public static final ResourceLocation tex_area = FTBU.mod.getLocation("textures/map/minimap_area.png");
	public static final ResourceLocation tex_map_entity = FTBU.mod.getLocation("textures/map/entity.png");
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
	
	public static int textureID = -1;
	public static ByteBuffer pixelBuffer = null;
	
	public final LMPlayerClient playerLM;
	public final int currentDim, startX, startY;
	
	public final ButtonLM buttonRefresh, buttonClose, buttonBlockLevel;
	public final ItemButtonLM buttonExplosions;
	public final MapButton mapButtons[];
	public final PanelLM panelButtons;
	
	public ThreadReloadChunk thread = null;
	
	public GuiMinimap()
	{
		super(null, null);
		hideNEI = true;
		xSize = ySize = tiles * 16;
		
		playerLM = LMWorldClient.inst.getClientPlayer();
		startX = MathHelperLM.chunk(mc.thePlayer.posX - tiles * 8D);
		startY = MathHelperLM.chunk(mc.thePlayer.posZ - tiles * 8D);
		currentDim = LatCoreMCClient.getDim();
		
		buttonClose = new ButtonLM(this, 0, 0, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				mc.displayGuiScreen(new GuiFriends(null));
			}
		};
		
		buttonRefresh = new ButtonLM(this, 0, 16, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				thread = new ThreadReloadChunk(mc.theWorld, GuiMinimap.this);
				thread.start();
				new MessageAreaRequest(startX, startY, tiles, tiles).sendToServer();
				gui.playClickSound();
			}
		};
		
		buttonRefresh.title = FTBULang.button_refresh();
		
		buttonExplosions = new ItemButtonLM(this, 0, 32, 16, 16, new ItemStack(Blocks.tnt))
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				if(LatCoreMCClient.isPlaying())
					ClientAction.ACTION_EXPLOSIONS.send(playerLM.settings.explosions ? 0 : 1);
			}
			
			public void addMouseOverText(FastList<String> l)
			{
				l.add(FTBU.mod.translateClient("button.explosions"));
				if(FTBUConfigClaims.forcedExplosions.get() != -1)
					l.add(FTBULang.label_server_forced(Boolean.toString(FTBUConfigClaims.forcedExplosions.get() == 1)));
			}
		};
		
		buttonBlockLevel = new ButtonLM(this, 0, 48, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				if(LatCoreMCClient.isPlaying())
					ClientAction.ACTION_SET_CLAIM_BLOCKS.send(b == 0 ? 0 : 1);
			}
			
			public void addMouseOverText(FastList<String> l)
			{
				l.add(title);
				l.add(playerLM.settings.blocks.getText());
				
				if(FTBUConfigClaims.forcedChunkSecurity.get() != -1)
					l.add(FTBULang.label_server_forced(LMSecurityLevel.VALUES_3[FTBUConfigClaims.forcedChunkSecurity.get()].getText()));
			}
		};
		
		buttonBlockLevel.title = FTBU.mod.translateClient("button.block_security");
		
		panelButtons = new PanelLM(this, 0, 0, 16, 0)
		{
			public void addWidgets()
			{
				add(buttonClose);
				add(buttonRefresh);
				add(buttonExplosions);
				add(buttonBlockLevel);
				height = widgets.size() * 16;
			}
			
			public int getAX()
			{ return gui.width - 16; }
			
			public int getAY()
			{ return 0; }
		};
		
		mapButtons = new MapButton[tiles * tiles];
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
		if(currentDim != LatCoreMCClient.getDim() || !LatCoreMCClient.isPlaying())
		{ mc.thePlayer.closeScreen(); return; }
		
		if(pixelBuffer != null)
		{
			if(textureID == -1)
			{
				textureID = TextureUtil.glGenTextures();
				new MessageAreaRequest(startX, startY, tiles, tiles).sendToServer();
			}
			
			//boolean hasBlur = false;
			//int filter = hasBlur ? GL11.GL_LINEAR : GL11.GL_NEAREST;
			int filter = GL11.GL_NEAREST;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, tiles * 16, tiles * 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
			pixelBuffer = null;
			thread = null;
		}
		
		drawBlankRect(guiLeft - 2, guiTop - 2, zLevel, xSize + 4, ySize + 4, 0xFF000000);
		//drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128, 0xFFFF0000);
		
		if(textureID != -1 && thread == null)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			drawTexturedRectD(guiLeft, guiTop, zLevel, tiles * 16, tiles * 16, 0D, 0D, 1D, 1D);
		}
		
		super.drawBackground();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		//setTexture(tex);
		
		renderMinimap();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		for(int i = 0; i < mapButtons.length; i++)
			mapButtons[i].renderWidget();
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		buttonRefresh.render(GuiIcons.refresh);
		buttonClose.render(GuiIcons.accept);
		buttonExplosions.renderWidget();
		buttonBlockLevel.render(playerLM.settings.blocks.getIcon());
		
		if(!playerLM.settings.explosions)
		{
			zLevel = 500F;
			GL11.glColor4f(1F, 1F, 1F, 0.75F);
			buttonExplosions.render(GuiIcons.close);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			zLevel = 0F;
		}
	}
	
	public void drawText(FastList<String> l)
	{
		String s = playerLM.claimedChunks + " / " + playerLM.maxClaimPower;
		s = FTBU.mod.translateClient("label.cchunks_count", s);
		fontRendererObj.drawString(s, width - fontRendererObj.getStringWidth(s) - 4, height - 12, 0xFFFFFFFF);
		super.drawText(l);
	}
	
	public void onLMGuiClosed()
	{
	}
	
	private ChunkType getType(int cx, int cy)
	{ return ClaimedAreasClient.getTypeE(currentDim, cx, cy); }
	
	@SuppressWarnings("unchecked")
	public void renderMinimap()
	{
		FTBLibClient.setTexture(tex_area);
		
		for(int y = 0; y < tiles; y++)
		for(int x = 0; x < tiles; x++)
		{
			int cx = x + startX;
			int cy = y + startY;
			
			ChunkType type = getType(cx, cy);
			if(type.drawGrid())
			{
				boolean a = type.equals(getType(cx, cy - 1));
				boolean b = type.equals(getType(cx + 1, cy));
				boolean c = type.equals(getType(cx, cy + 1));
				boolean d = type.equals(getType(cx - 1, cy));
				
				TextureCoords tc = tex_area_coords[a ? 1 : 0][b ? 1 : 0][c ? 1 : 0][d ? 1 : 0];
				
				FTBLibClient.setGLColor(type.getAreaColor(playerLM), 255);
				GuiLM.drawTexturedRectD(guiLeft + x * 16, guiTop + y * 16, zLevel, 16, 16, tc.minU, tc.minV, tc.maxU, tc.maxV);
			}
		}
		
		if(!FTBLibClient.mc.theWorld.playerEntities.isEmpty())
		{
			FastList<EntityPlayer> list = new FastList<EntityPlayer>();
			list.addAll(FTBLibClient.mc.theWorld.playerEntities);
			
			for(int i = 0; i < list.size(); i++)
			{
				EntityPlayer ep = list.get(i);
				if(ep.dimension == currentDim && !ep.isInvisible())
				{
					int cx = MathHelperLM.chunk(ep.posX);
					int cy = MathHelperLM.chunk(ep.posZ);
					
					if(cx >= startX && cy >= startY && cx < startX + tiles && cy < startY + tiles)
					{
						double x = ((cx - startX) * 16D + MathHelperLM.wrap(ep.posX, 16D));
						double y = ((cy - startY) * 16D + MathHelperLM.wrap(ep.posZ, 16D));
						
						GL11.glPushMatrix();
						GL11.glTranslated(guiLeft + x, guiTop + y, 0D);
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
	}
	
	public static class MapButton extends ButtonLM
	{
		public final GuiMinimap gui;
		public final int chunkX, chunkY;
		
		public MapButton(GuiMinimap g, int x, int y, int i)
		{
			super(g, x, y, 16, 16);
			gui = g;
			posX += (i % tiles) * width;
			posY += (i / tiles) * height;
			chunkX = g.startX + (i % tiles);
			chunkY = g.startY + (i / tiles);
		}
		
		public void onButtonPressed(int b)
		{
			if(gui.panelButtons.mouseOver()) return;
			new MessageClaimChunk(gui.currentDim, chunkX, chunkY, b == 0).sendToServer();
			gui.playClickSound();
		}
		
		public void addMouseOverText(FastList<String> l)
		{ ClaimedAreasClient.getMessage(gui.currentDim, chunkX, chunkY, l, isShiftKeyDown()); }
		
		public void renderWidget()
		{
			if(mouseOver())
			{
				GL11.glColor4f(1F, 1F, 0.3F, 0.8F);
				gui.render(tex_area_coords[0][0][0][0], getAX(), getAY(), 16, 16);
			}
		}
	}
}