package latmod.ftbu.mod.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.client.minimap.*;
import latmod.ftbu.net.*;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.LMWorldClient;
import latmod.lib.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class GuiMinimap extends GuiLM // implements IClientActionGui
{
	//public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/minimap.png");
	//public static final TextureCoords tex_mouse = new TextureCoords(tex, 156, 0, 32, 32);
	private static final MRenderer mapRenderer = new MRenderer();
	
	public final Minimap minimap;
	
	public int tileSize = 32;
	public final ButtonLM buttonRefresh, buttonClose;
	public final ItemButtonLM buttonSafe;
	public final FastList<MapButton> mapButtons;
	
	public GuiMinimap()
	{
		super(null, null);
		hideNEI = true;
		
		tileSize = 16;
		
		minimap = Minimap.get(LatCoreMCClient.getDim());
		
		mapRenderer.renderClaims = true;
		mapRenderer.renderGrid = Minimap.mapOptions.hasGrid();
		mapRenderer.renderPlayers = true;
		mapRenderer.renderAreaTitle = false;
		
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
				minimap.reloadArea(mc.theWorld, mapRenderer.startX, mapRenderer.startY, mapRenderer.tilesX, mapRenderer.tilesY, true);
				gui.playClickSound();
			}
		};
		
		buttonRefresh.title = FTBULang.button_refresh();
		
		buttonSafe = new ItemButtonLM(this, 16, 0, 32, 32)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				if(LatCoreMCClient.isPlaying())
					ClientAction.ACTION_SET_SAFE_CHUNKS.send(LMWorldClient.inst.clientPlayer.settings.safeClaims ? 0 : 1);
			}
		};
		
		buttonSafe.setItem(new ItemStack(Blocks.tnt));
		
		mapButtons = new FastList<MapButton>();
		buttonRefresh.onButtonPressed(0);
	}
	
	public void initLMGui()
	{
		xSize = width;
		ySize = height;
		
		mapRenderer.tileSize = tileSize;
		
		mapRenderer.tilesX = (int)((double)xSize / mapRenderer.tileSize) + 1;
		mapRenderer.tilesY = (int)((double)ySize / mapRenderer.tileSize);
		mapRenderer.startX = MathHelperLM.chunk(mc.thePlayer.posX) - mapRenderer.tilesX / 2;
		mapRenderer.startY = MathHelperLM.chunk(mc.thePlayer.posZ) - mapRenderer.tilesY / 2;
		
		mapButtons.clear();
		for(int i = 0; i < mapRenderer.tilesX * mapRenderer.tilesY; i++)
			mapButtons.add(new MapButton(this, 0, 32));
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
		drawBlankRect(0, 0, zLevel, xSize, ySize, 0xFF000000);
		
		drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128, 0xFFFF0000);
		
		if(mapButtons.isEmpty()) return;
		
		if(mouseDWheel != 0)
		{
			if(mouseDWheel < 0) tileSize /= 2D;
			else tileSize *= 2D;
			if(tileSize < 8) tileSize = 8;
			else if(tileSize > 64) tileSize = 64;
			else initLMGui();
		}
		
		super.drawBackground();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		//setTexture(tex);
		
		mapRenderer.renderX = mapButtons.get(0).getAX();
		mapRenderer.renderY = mapButtons.get(0).getAY();
		//mapRenderer.render();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		for(int i = 0; i < mapButtons.size(); i++)
			mapButtons.get(i).renderWidget();
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		buttonRefresh.render(GuiIcons.map);
		buttonClose.render(GuiIcons.accept);
		buttonSafe.render();
		
		if(LMWorldClient.inst.clientPlayer.settings.safeClaims)
		{
			zLevel = 500F;
			GL11.glColor4f(1F, 1F, 1F, 0.75F);
			buttonSafe.render(GuiIcons.close);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			zLevel = 0F;
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
	
	public static class MapButton extends ButtonLM
	{
		public final GuiMinimap gui;
		public final MChunk chunk;
		
		public MapButton(GuiMinimap g, int x, int y)
		{
			super(g, x, y, g.tileSize, g.tileSize);
			gui = g;
			int i = g.mapButtons.size();
			posX += (i % mapRenderer.tilesX) * width;
			posY += (i / mapRenderer.tilesX) * height;
			int chunkX = mapRenderer.startX + (i % mapRenderer.tilesX);
			int chunkY = mapRenderer.startY + (i / mapRenderer.tilesX);
			chunk = gui.minimap.loadChunk(chunkX, chunkY);
		}
		
		public void onButtonPressed(int b)
		{
			new MessageClaimChunk(gui.minimap.dim, mapRenderer.startX, mapRenderer.startY, b == 0).sendToServer();
			gui.playClickSound();
		}
		
		public void addMouseOverText(FastList<String> l)
		{ chunk.getMessage(l, isShiftKeyDown()); }
		
		public void renderWidget()
		{
			if(mouseOver())
			{
				GL11.glColor4f(0.1F, 1F, 0.7F, 0.8F);
				drawBlankRect(getAX(), getAY(), gui.zLevel, gui.tileSize, gui.tileSize, 0x3311FF66);
			}
		}
	}
}