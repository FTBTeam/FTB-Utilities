package latmod.ftbu.util.gui;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.*;
import ftb.lib.OtherMods;
import ftb.lib.client.*;
import latmod.ftbu.util.client.LMRenderHelper;
import latmod.lib.*;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = OtherMods.NEI)
public abstract class GuiLM extends GuiContainer implements codechicken.nei.api.INEIGuiHandler
{
	private static final FastList<String> tempTextList = new FastList<String>();
	
	// GuiLM //
	
	public final ContainerLM container;
	public final ResourceLocation texture;
	public final PanelLM mainPanel;
	public int mouseX, mouseY, mouseDWheel, mouseDX, mouseDY, lastClickX, lastClickY;
	public float delta;
	
	public boolean hideNEI = false;
	private ResourceLocation prevTexture = null;
	private boolean refreshWidgets = true;
	
	public GuiLM(ContainerLM c, ResourceLocation tex)
	{
		super((c == null) ? new ContainerEmpty(FTBLibClient.mc.thePlayer, null) : c);
		mc = FTBLibClient.mc;
		mainPanel = new PanelLM(this, 0, 0, 0, 0)
		{
			public void addWidgets()
			{ GuiLM.this.addWidgets(); }
		};
		refreshWidgets();
		container = (ContainerLM)inventorySlots;
		texture = tex;
	}
	
	public void refreshWidgets()
	{ refreshWidgets = true; }
	
	public abstract void addWidgets();
	
	public ItemStack getHeldItem()
	{ return container.player.inventory.getItemStack(); }
	
	public final int getPosX(int x)
	{ return guiLeft + x; }
	
	public final int getPosY(int y)
	{ return guiTop + y; }
	
	public final float getZLevel()
	{ return zLevel; }
	
	public final void setZLevel(float z)
	{ zLevel = z; }
	
	public final int getWidth()
	{ return xSize; }
	
	public final int getHeight()
	{ return ySize; }
	
	public final void setTexture(ResourceLocation tex)
	{
		if(prevTexture != tex)
		{
			prevTexture = tex;
			if(tex != null) mc.getTextureManager().bindTexture(tex);
		}
	}
	
	public final void initGui()
	{
		super.initGui();
		initLMGui();
		mainPanel.width = xSize;
		mainPanel.height = ySize;
		mainPanel.posX = guiLeft = (width - xSize) / 2;
		mainPanel.posY = guiTop = (height - ySize) / 2;
		refreshWidgets();
	}
	
	public void initLMGui()
	{
	}
	
	protected final void mouseClicked(int mx, int my, int b)
	{
		lastClickX = mouseX = mx;
		lastClickY = mouseY = my;
		mainPanel.mousePressed(b);
		super.mouseClicked(mx, my, b);
		mouseClicked(b);
	}
	
	public void mouseClicked(int b)
	{
	}
	
	protected void keyTyped(char keyChar, int key)
	{
		if(mainPanel.keyPressed(key, keyChar)) return;
		super.keyTyped(keyChar, key);
	}
	
	public final void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{ drawBackground(); }
	
	public void drawBackground()
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		if(texture != null)
		{
			setTexture(texture);
			drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}
	
	public final void drawGuiContainerForegroundLayer(int mx, int my)
	{ drawForeground(); }
	
	public void drawForeground()
	{
	}
	
	public final void drawScreen(int mx, int my, float f)
	{
		mouseDX = mx - mouseX;
		mouseDY = my - mouseY;
		mouseX = mx;
		mouseY = my;
		delta = f;
		prevTexture = null;
		mouseDWheel = Mouse.getDWheel();
		
		if(refreshWidgets)
		{
			mainPanel.refreshWidgets();
			refreshWidgets = false;
		}
		
		super.drawScreen(mx, my, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		tempTextList.clear();
		drawText(tempTextList);
		
		if(!tempTextList.isEmpty())
			drawHoveringText(tempTextList, mouseX, mouseY, fontRendererObj);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		
		drawForeground();
	}
	
	public void drawText(FastList<String> l)
	{
		mainPanel.addMouseOverText(l);
	}
	
	public final void onGuiClosed()
	{
		super.onGuiClosed();
		onLMGuiClosed();
	}
	
	public void onLMGuiClosed()
	{
	}
	
	public void drawWrappedIcon(IIcon i, float x, float y, float w, float h)
	{ drawTexturedRectD(x, y, zLevel, w, h, i.getMinU(), i.getMinV(), i.getMaxU(), i.getMaxV()); }
	
	public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h)
	{ drawTexturedModalRectD(x, y, u, v, w, h); }
	
	public void drawTexturedModalRectD(double x, double y, double u, double v, double w, double h)
	{ drawTexturedModalRectD(x, y, zLevel, u, v, w, h, 256, 256); }
	
	public static void drawTexturedModalRectD(double x, double y, double z, double u, double v, double w, double h, int width, int height)
	{ drawTexturedRect(x, y, z, w, h, u, v, u + w, v + h, width, height); }
	
	public static void drawTexturedRectD(double x, double y, double z, double w, double h, double u0, double v0, double u1, double v1)
	{
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		
		if(u0 == 0D && v0 == 0D && u1 == 0D && v1 == 0D)
		{
			t.addVertex(x + 0, y + h, z);
			t.addVertex(x + w, y + h, z);
			t.addVertex(x + w, y + 0, z);
			t.addVertex(x + 0, y + 0, z);
		}
		else
		{
			t.addVertexWithUV(x + 0, y + h, z, u0, v1);
			t.addVertexWithUV(x + w, y + h, z, u1, v1);
			t.addVertexWithUV(x + w, y + 0, z, u1, v0);
			t.addVertexWithUV(x + 0, y + 0, z, u0, v0);
		}
		t.draw();
	}
	
	public static void drawTexturedRect(double x, double y, double z, double w, double h, double u0, double v0, double u1, double v1, int textureW, int textureH)
	{
		double scX = 1D / (double)textureW;
		double scY = 1D / (double)textureH;
		drawTexturedRectD(x, y, z, w, h, u0 * scX, v0 * scY, u1 * scX, v1 * scY);
	}
	
	public void playSoundFX(ResourceLocation s, float pitch)
	{ mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(s, pitch)); }
	
	public void playClickSound()
	{ FTBLibClient.playClickSound(); }
	
	public FontRenderer getFontRenderer()
	{ setTexture(null); return fontRendererObj; }
	
	public static void drawPlayerHead(String username, double x, double y, double w, double h, double z)
	{
		FTBLibClient.setTexture(FTBLibClient.getSkinTexture(username));
		drawTexturedRectD(x, y, z, w, h, 0.125D, 0.25D, 0.25D, 0.5D);
		drawTexturedRectD(x, y, z, w, h, 0.625D, 0.25D, 0.75D, 0.5D);
	}
	
	public static void drawBlankRect(double x, double y, double z, double w, double h, int col)
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setColorRGBA_I(col, LMColorUtils.getAlpha(col));
		t.addVertex(x + 0, y + h, z);
		t.addVertex(x + w, y + h, z);
		t.addVertex(x + w, y + 0, z);
		t.addVertex(x + 0, y + 0, z);
		t.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void drawItem(ItemStack is, int x, int y)
	{
		if(is == null) return;
		setTexture(TextureMap.locationItemsTexture);
		zLevel = 200F;
		itemRender.zLevel = 200F;
		LMRenderHelper.renderGuiItem(is, itemRender, getFontRenderer(), x, y);
		zLevel = 0F;
		itemRender.zLevel = 0F;
	}
	
	public void render(TextureCoords tc, double x, double y, double w, double h)
	{
		if(tc == null || !tc.isValid()) return;
		setTexture(tc.texture);
		GuiLM.drawTexturedRectD(x, y, zLevel, w, h, tc.minU, tc.minV, tc.maxU, tc.maxV);
	}
	
	public void render(TextureCoords tc, double x, double y)
	{ if(tc != null && tc.isValid()) render(tc, x, y, tc.width, tc.height); }

	@Optional.Method(modid = OtherMods.NEI)
	public codechicken.nei.VisiblityData modifyVisiblity(GuiContainer g, codechicken.nei.VisiblityData vd)
	{ if(hideNEI) vd.showNEI = false; return vd; }
	
	public Iterable<Integer> getItemSpawnSlots(GuiContainer g, ItemStack is)
	{ return null; }
	
	@Optional.Method(modid = OtherMods.NEI)
	public List<codechicken.nei.api.TaggedInventoryArea> getInventoryAreas(GuiContainer paramGuiContainer)
	{ return null; }
	
	public boolean handleDragNDrop(GuiContainer g, int x, int y, ItemStack is, int b)
	{ return false; }
	
	public boolean hideItemPanelSlot(GuiContainer g, int x, int y, int w, int h)
	{ return hideNEI; }
}