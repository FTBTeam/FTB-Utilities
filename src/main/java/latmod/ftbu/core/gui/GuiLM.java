package latmod.ftbu.core.gui;
import java.util.List;

import latmod.ftbu.core.OtherMods;
import latmod.ftbu.core.client.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = OtherMods.NEI)
public abstract class GuiLM extends GuiContainer implements codechicken.nei.api.INEIGuiHandler
{
	// General IIcons //
	
	public static class Icons
	{
		public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/icons.png");
		
		public static final TextureCoords left = new TextureCoords(tex, 0);
		public static final TextureCoords right = new TextureCoords(tex, 1);
		public static final TextureCoords accept = new TextureCoords(tex, 2);
		public static final TextureCoords add = new TextureCoords(tex, 3);
		public static final TextureCoords remove = new TextureCoords(tex, 4);
		public static final TextureCoords info = new TextureCoords(tex, 5);
		public static final TextureCoords sort = new TextureCoords(tex, 6);
		public static final TextureCoords friends = new TextureCoords(tex, 7);
		public static final TextureCoords bug = new TextureCoords(tex, 8);
		public static final TextureCoords jacket = new TextureCoords(tex, 9);
		public static final TextureCoords up = new TextureCoords(tex, 10);
		public static final TextureCoords down = new TextureCoords(tex, 11);
		public static final TextureCoords button = new TextureCoords(tex, 12);
		public static final TextureCoords pressed = new TextureCoords(tex, 13);
		public static final TextureCoords player = new TextureCoords(tex, 14);
		public static final TextureCoords online = new TextureCoords(tex, 15);
		
		public static final TextureCoords settings = new TextureCoords(tex, 16);
		public static final TextureCoords bed = new TextureCoords(tex, 17);
		public static final TextureCoords bell = new TextureCoords(tex, 18);
		public static final TextureCoords compass = new TextureCoords(tex, 19);
		public static final TextureCoords map = new TextureCoords(tex, 20);
		public static final TextureCoords shield = new TextureCoords(tex, 21);
		public static final TextureCoords picture = new TextureCoords(tex, 22);
		public static final TextureCoords moneybag = new TextureCoords(tex, 23);
		public static final TextureCoords game = new TextureCoords(tex, 24);
		public static final TextureCoords feather = new TextureCoords(tex, 25);
		public static final TextureCoords camera = new TextureCoords(tex, 26);
		public static final TextureCoords cancel = new TextureCoords(tex, 27);
		public static final TextureCoords accept_gray = new TextureCoords(tex, 28);
		public static final TextureCoords add_gray = new TextureCoords(tex, 29);
		public static final TextureCoords remove_gray = new TextureCoords(tex, 30);
		public static final TextureCoords info_gray = new TextureCoords(tex, 31);
		
		public static final TextureCoords[] inv =
		{
			new TextureCoords(tex, 32),
			new TextureCoords(tex, 33),
			new TextureCoords(tex, 34),
			new TextureCoords(tex, 35),
		};
		
		public static final TextureCoords[] redstone =
		{
			new TextureCoords(tex, 36),
			new TextureCoords(tex, 37),
			new TextureCoords(tex, 38),
			new TextureCoords(tex, 39),
		};
		
		public static final TextureCoords[] security =
		{
			new TextureCoords(tex, 40),
			new TextureCoords(tex, 41),
			new TextureCoords(tex, 42),
			new TextureCoords(tex, 43),
		};
		
		public static final TextureCoords back = new TextureCoords(tex, 44);
		public static final TextureCoords close = new TextureCoords(tex, 45);
		public static final TextureCoords player_gray = new TextureCoords(tex, 46);
		public static final TextureCoords online_red = new TextureCoords(tex, 47);
		public static final TextureCoords notes = new TextureCoords(tex, 48);
		public static final TextureCoords hsb = new TextureCoords(tex, 49);
		public static final TextureCoords rgb = new TextureCoords(tex, 50);
		public static final TextureCoords comment = new TextureCoords(tex, 51);
		public static final TextureCoords bin = new TextureCoords(tex, 52);
		public static final TextureCoords marker = new TextureCoords(tex, 53);
		public static final TextureCoords beacon = new TextureCoords(tex, 54);
		public static final TextureCoords color_blank = new TextureCoords(tex, 55);
	}
	
	private static final FastList<String> tempTextList = new FastList<String>();
	
	// GuiLM //
	
	public final ContainerLM container;
	public final ResourceLocation texture;
	private final FastList<WidgetLM> widgets;
	private boolean refreshWidgets = true;
	public int mouseX, mouseY, mouseXR, mouseYR, mouseDWheel;
	public float delta;
	
	public boolean hideNEI = false;
	private ResourceLocation prevTexture = null;
	public static GuiLM currentGui = null;
	
	public GuiLM(ContainerLM c, ResourceLocation tex)
	{
		super((c == null) ? new ContainerEmpty(LatCoreMCClient.getMinecraft().thePlayer, null) : c);
		mc = LatCoreMCClient.getMinecraft();
		refreshWidgets();
		
		container = (ContainerLM)inventorySlots;
		texture = tex;
		widgets = new FastList<WidgetLM>();
	}
	
	public void refreshWidgets()
	{ refreshWidgets = true; }
	
	public FastList<WidgetLM> getWidgets()
	{ return widgets; }
	
	public abstract void addWidgets(FastList<WidgetLM> l);
	
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
		currentGui = this;
		super.initGui();
		initLMGui();
		refreshWidgets();
	}
	
	public void initLMGui()
	{
	}
	
	protected void mouseClicked(int mx, int my, int b)
	{
		for(WidgetLM w : widgets)
			if(w.isEnabled()) w.mousePressed(b);
		super.mouseClicked(mx, my, b);
	}
	
	protected void keyTyped(char keyChar, int key)
	{
		for(WidgetLM w : widgets)
			if(w.isEnabled() && w.keyPressed(key, keyChar)) return;
		
		super.keyTyped(keyChar, key);
	}
	
	public final void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{ drawBackground(); }
	
	public void drawBackground()
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		LMColorUtils.recolor();
		setTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	public final void drawGuiContainerForegroundLayer(int mx, int my)
	{ drawForeground(); }
	
	public void drawForeground()
	{
	}
	
	public final void drawScreen(int mx, int my, float f)
	{
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		mouseX = mx;
		mouseY = my;
		mouseXR = mx - guiLeft;
		mouseYR = my - guiTop;
		delta = f;
		prevTexture = null;
		mouseDWheel = Mouse.getDWheel();
		
		if(refreshWidgets)
		{
			widgets.clear();
			addWidgets(widgets);
			widgets.removeNullValues();
			
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
		for(int i = 0; i < widgets.size(); i++)
		{
			WidgetLM w = widgets.get(i);
			if(w.isEnabled() && w.mouseOver())
				w.addMouseOverText(l);
		}
	}
	
	public final void onGuiClosed()
	{
		super.onGuiClosed();
		onLMGuiClosed();
		currentGui = null;
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
	{ LatCoreMCClient.playClickSound(); }
	
	public FontRenderer getFontRenderer()
	{ return fontRendererObj; }
	
	public static void drawPlayerHead(String username, double x, double y, double w, double h, double z)
	{
		LatCoreMCClient.setTexture(LatCoreMCClient.getSkinTexture(username));
		
		Tessellator tessellator = Tessellator.instance;
		
		double minU = 1D / 8D;
		double minV = 1D / 4D;
		double maxU = 2D / 8D;
		double maxV = 2D / 4D;
		
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, z, minU, maxV);
		tessellator.addVertexWithUV(x + w, y + h, z, maxU, maxV);
		tessellator.addVertexWithUV(x + w, y + 0, z, maxU, minV);
		tessellator.addVertexWithUV(x + 0, y + 0, z, minU, minV);
		tessellator.draw();
		
		double minU2 = 5D / 8D;
		double minV2 = 1D / 4D;
		double maxU2 = 6D / 8D;
		double maxV2 = 2D / 4D;
		
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, z, minU2, maxV2);
		tessellator.addVertexWithUV(x + w, y + h, z, maxU2, maxV2);
		tessellator.addVertexWithUV(x + w, y + 0, z, maxU2, minV2);
		tessellator.addVertexWithUV(x + 0, y + 0, z, minU2, minV2);
		tessellator.draw();
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