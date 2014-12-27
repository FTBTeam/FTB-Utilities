package latmod.core.gui;
import latmod.core.FastList;
import latmod.core.client.LMRenderHelper;
import latmod.core.mod.LC;
import latmod.core.mod.client.LCClient;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class GuiLM extends GuiContainer
{
	public static final ResourceLocation icons_lm = LC.mod.getLocation("textures/gui/icons_lm.png");
	private static final int BS = 19;
	public static final TextureCoords
	
	button_basic =			new TextureCoords(icons_lm, BS * 0, BS * 0),
	button_pressed =		new TextureCoords(icons_lm, BS * 0, BS * 1),
	button_over =			new TextureCoords(icons_lm, BS * 0, BS * 2),
	button_inner =			new TextureCoords(icons_lm, BS * 0, BS * 3),
	button_inner_pressed =	new TextureCoords(icons_lm, BS * 0, BS * 4),
	
	button_back =			new TextureCoords(icons_lm, BS * 1, BS * 0),
	button_help =			new TextureCoords(icons_lm, BS * 1, BS * 1),
	button_settings =		new TextureCoords(icons_lm, BS * 1, BS * 2);
	
	public static final TextureCoords[] button_security =
	{
		new TextureCoords(icons_lm, BS * 2, BS * 0),
		new TextureCoords(icons_lm, BS * 2, BS * 1),
		new TextureCoords(icons_lm, BS * 2, BS * 2),
		new TextureCoords(icons_lm, BS * 2, BS * 3),
	};
	
	public static final TextureCoords[] button_inv =
	{
		new TextureCoords(icons_lm, BS * 3, BS * 0),
		new TextureCoords(icons_lm, BS * 3, BS * 1),
		new TextureCoords(icons_lm, BS * 3, BS * 2),
		new TextureCoords(icons_lm, BS * 3, BS * 3),
	};
	
	public static final TextureCoords[] button_redstone =
	{
		new TextureCoords(icons_lm, BS * 4, BS * 0),
		new TextureCoords(icons_lm, BS * 4, BS * 1),
		new TextureCoords(icons_lm, BS * 4, BS * 2),
		new TextureCoords(icons_lm, BS * 4, BS * 3),
	};
	
	public final ContainerLM container;
	public final ResourceLocation texture;
	public final FastList<WidgetLM> widgets;
	
	public GuiLM(ContainerLM c, ResourceLocation tex)
	{
		super(c);
		container = c;
		texture = tex;
		widgets = new FastList<WidgetLM>();
	}
	
	public ItemStack getHeldItem()
	{ return container.player.inventory.getItemStack(); }
	
	public final int getPosX()
	{ return guiLeft; }
	
	public final int getPosY()
	{ return guiTop; }
	
	public final float getZLevel()
	{ return zLevel; }
	
	public final void setZLevel(float z)
	{ zLevel = z; }
	
	public final void setTexture(ResourceLocation tex)
	{ mc.getTextureManager().bindTexture(tex); }
	
	protected void mouseClicked(int mx, int my, int b)
	{
		for(int i = 0; i < widgets.size(); i++)
			widgets.get(i).voidMousePressed(mx, my, b);
		
		for(int i = 0; i < widgets.size(); i++)
		{
			if(widgets.get(i).mousePressed(mx, my, b))
				return;
		}
		
		super.mouseClicked(mx, my, b);
	}
	
	protected void keyTyped(char keyChar, int key)
	{
		for(int i = 0; i < widgets.size(); i++)
		{
			if(widgets.get(i).keyPressed(key, keyChar))
				return;
		}
		
		super.keyTyped(keyChar, key);
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		LMRenderHelper.recolor();
		setTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	public void drawWrappedIcon(IIcon i, float x, float y, float w, float h)
	{
		float minU = i.getMinU();
		float minV = i.getMinV();
		float maxU = i.getMaxU();
		float maxV = i.getMaxV();
		
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.addVertexWithUV(x + 0, y + h, zLevel, minU, maxV);
		t.addVertexWithUV(x + w, y + h, zLevel, maxU, maxV);
		t.addVertexWithUV(x + w, y + 0, zLevel, maxU, minV);
		t.addVertexWithUV(x + 0, y + 0, zLevel, minU, minV);
		t.draw();
	}
	
	public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h)
	{ drawTexturedModalRectD(x, y, u, v, w, h); }
	
	public void drawTexturedModalRectD(double x, double y, double u, double v, double w, double h)
	{
		double scX = 1D / 256D;
		double scY = 1D / 256D;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, zLevel, (u + 0) * scX, (v + h) * scY);
		tessellator.addVertexWithUV(x + w, y + h, zLevel, (u + w) * scX, (v + h) * scY);
		tessellator.addVertexWithUV(x + w, y + 0, zLevel, (u + w) * scX, (v + 0) * scY);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (u + 0) * scX, (v + 0) * scY);
		tessellator.draw();
	}
	
	public void playSoundFX(String s, float pitch)
	{ mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(s), pitch)); }
	
	public void playClickSound()
	{ playSoundFX("gui.button.press", 1F); }
	
	public FontRenderer getFontRenderer()
	{ return fontRendererObj; }
	
	public void drawPlayerHead(String username, double x, double y, double w, double h)
	{
		setTexture(LCClient.getSkinTexture(username));
		
		Tessellator tessellator = Tessellator.instance;
		
		double minU = 1D / 8D;
		double minV = 1D / 4D;
		double maxU = 2D / 8D;
		double maxV = 2D / 4D;
		
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, zLevel, minU, maxV);
		tessellator.addVertexWithUV(x + w, y + h, zLevel, maxU, maxV);
		tessellator.addVertexWithUV(x + w, y + 0, zLevel, maxU, minV);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, minU, minV);
		tessellator.draw();
		
		double minU2 = 5D / 8D;
		double minV2 = 1D / 4D;
		double maxU2 = 6D / 8D;
		double maxV2 = 2D / 4D;
		
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, zLevel, minU2, maxV2);
		tessellator.addVertexWithUV(x + w, y + h, zLevel, maxU2, maxV2);
		tessellator.addVertexWithUV(x + w, y + 0, zLevel, maxU2, minV2);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, minU2, minV2);
		tessellator.draw();
	}
}