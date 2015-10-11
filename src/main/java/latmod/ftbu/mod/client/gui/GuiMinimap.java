package latmod.ftbu.mod.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.client.minimap.*;
import latmod.ftbu.net.*;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.LMWorldClient;
import latmod.lib.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiMinimap extends GuiLM // implements IClientActionGui
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/minimap.png");
	public static final TextureCoords tex_mouse = new TextureCoords(tex, 156, 0, 32, 32);
	private static final MRenderer mapRenderer = new MRenderer();
	private static final int TILES = 9;
	
	public final Minimap minimap;
	public MapButton buttonPressed0 = null;
	
	public final ButtonLM buttonRefresh, buttonClose;
	public final ItemButtonLM buttonSafe;
	public final MapButton[] mapButtons;
	
	public GuiMinimap()
	{
		super(null, tex);
		xSize = 156;
		ySize = 185;
		hideNEI = true;
		
		minimap = Minimap.get(LatCoreMCClient.getDim());
		
		mapRenderer.tiles = TILES;
		mapRenderer.size = TILES * 16;
		mapRenderer.startX = MathHelperLM.chunk(mc.thePlayer.posX) - TILES / 2;
		mapRenderer.startY = MathHelperLM.chunk(mc.thePlayer.posZ) - TILES / 2;
		
		mapRenderer.renderClaims = true;
		mapRenderer.renderGrid = Minimap.mapOptions.hasGrid();
		mapRenderer.renderPlayers = true;
		mapRenderer.renderAreaTitle = false;
		
		mapRenderer.reload();
		
		buttonRefresh = new ButtonLM(this, 6, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				Minimap m = Minimap.get(mc.thePlayer.dimension);
				m.reloadArea(mc.theWorld, mapRenderer.startX, mapRenderer.startY, TILES, TILES);
				gui.playClickSound();
			}
		};
		
		buttonRefresh.title = FTBULang.button_refresh();
		
		buttonClose = new ButtonLM(this, 132, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				mc.displayGuiScreen(new GuiFriends(null));
			}
		};
		
		buttonClose.title = FTBULang.button_close();
		
		buttonSafe = new ItemButtonLM(this, 28, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				if(LatCoreMCClient.isPlaying())
					ClientAction.ACTION_SET_SAFE_CHUNKS.send(LMWorldClient.inst.clientPlayer.settings.safeClaims ? 0 : 1);
			}
		};
		
		buttonSafe.setItem(new ItemStack(Items.skull, 1, 4));
		
		mapButtons = new MapButton[TILES * TILES];
		
		for(int i = 0; i < mapButtons.length; i++)
			mapButtons[i] = new MapButton(this, 6, 26, i);
		
		buttonRefresh.onButtonPressed(0);
	}
	
	public void addWidgets()
	{
		mainPanel.addAll(mapButtons);
		mainPanel.add(buttonRefresh);
		mainPanel.add(buttonClose);
		mainPanel.add(buttonSafe);
	}
	
	public void drawBackground()
	{
		if(minimap.dim != LatCoreMCClient.getDim() || !LatCoreMCClient.isPlaying())
		{ mc.thePlayer.closeScreen(); return; }
		
		super.drawBackground();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		setTexture(tex);
		
		mapRenderer.renderX = mapButtons[0].getAX();
		mapRenderer.renderY = mapButtons[0].getAY();
		mapRenderer.render();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		for(int i = 0; i < mapButtons.length; i++)
			mapButtons[i].renderWidget();
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		buttonRefresh.render(GuiIcons.map);
		buttonClose.render(GuiIcons.accept);
		buttonSafe.render();
		
		if(LMWorldClient.inst.clientPlayer.settings.safeClaims)
		{
			zLevel = 500;
			GL11.glColor4f(1F, 1F, 1F, 0.75F);
			buttonSafe.render(GuiIcons.close);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			zLevel = 0;
		}
		
		if(buttonPressed0 != null)
		{
			int x = Math.min(buttonPressed0.mouseCX, mouseX);
			int y = Math.min(buttonPressed0.mouseCY, mouseY);
			int w = Math.min(Math.abs(buttonPressed0.mouseCX - mouseX), mapRenderer.size);
			int h = Math.min(Math.abs(buttonPressed0.mouseCY - mouseY), mapRenderer.size);
			drawBlankRect(x, y, zLevel, w, h, 0x7700FF00);
		}
	}
	
	public void drawText(FastList<String> l)
	{
		String s = LMWorldClient.inst.clientPlayer.claimedChunks + " / " + LMWorldClient.inst.clientPlayer.maxClaimPower;
		fontRendererObj.drawString(s, guiLeft + xSize - fontRendererObj.getStringWidth(s) - 4, guiTop + ySize - 12, 0xFFFFFFFF);
		super.drawText(l);
	}
	
	public void onLMGuiClosed()
	{
	}
	
	public void onClientDataChanged()
	{
		//refreshWidgets();
	}
	
	protected void mouseMovedOrUp(int mx, int my, int b)
	{
		mouseX = mx;
		mouseY = my;
		
		if(b < 0 || b > 1) return;
		
		if(buttonPressed0 != null)
		{
			MapButton button = getMouseOverButton();
			
			if(button != null)
			{
				int cx = Math.min(buttonPressed0.chunk.posX, button.chunk.posX);
				int cy = Math.min(buttonPressed0.chunk.posY, button.chunk.posY);
				int csx = Math.abs(buttonPressed0.chunk.posX - button.chunk.posX) + 1;
				int csy = Math.abs(buttonPressed0.chunk.posY - button.chunk.posY) + 1;
				new MessageClaimChunk(minimap.dim, cx, cy, csx, csy, b == 0).sendToServer();
			}
			
			buttonPressed0 = null;
		}
		
		super.mouseMovedOrUp(mx, my, b);
	}
	
	private MapButton getMouseOverButton()
	{
		for(int i = 0; i < mapButtons.length; i++)
			if(mapButtons[i].mouseOver())
				return mapButtons[i];
		return null;
	}
	
	public static class MapButton extends ButtonLM
	{
		public final GuiMinimap gui;
		public final MChunk chunk;
		public int mouseCX, mouseCY;
		
		public MapButton(GuiMinimap g, int x, int y, int i)
		{
			super(g, x, y, 16, 16);
			gui = g;
			posX += (i % TILES) * width;
			posY += (i / TILES) * height;
			int chunkX = mapRenderer.startX + (i % TILES);
			int chunkY = mapRenderer.startY + (i / TILES);
			chunk = gui.minimap.loadChunk(chunkX, chunkY);
		}
		
		public void onButtonPressed(int b)
		{
			gui.buttonPressed0 = this;
			mouseCX = gui.mouseX;
			mouseCY = gui.mouseY;
			gui.playClickSound();
		}
		
		public void addMouseOverText(FastList<String> l)
		{ chunk.getMessage(l, isShiftKeyDown()); }
		
		public void renderWidget()
		{
			if(mouseOver())
			{
				GL11.glColor4f(0.1F, 1F, 0.7F, 0.8F);
				tex_mouse.render(gui, getAX(), getAY(), 16, 16);
			}
		}
	}
}