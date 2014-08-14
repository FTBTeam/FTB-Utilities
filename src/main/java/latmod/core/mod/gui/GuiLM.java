package latmod.core.mod.gui;
import cpw.mods.fml.relauncher.*;
import latmod.core.util.*;
import latmod.core.client.LMRenderer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class GuiLM extends GuiContainer
{
	public ContainerLM container;
	public ResourceLocation texture;
	public FastList<WidgetLM> widgets;
	
	public GuiLM(ContainerLM c)
	{
		super(c);
		container = c;
		texture = c.getTexture();
		widgets = new FastList<WidgetLM>();
	}
	
	public ItemStack getHeldItem()
	{ return container.player.inventory.getItemStack(); }
	
	public int getPosX()
	{ return guiLeft; }
	
	public int getPosY()
	{ return guiTop; }
	
	public double getZLevel()
	{ return zLevel; }
	
	public void setTexture(ResourceLocation tex)
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
	
	public void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
	{
		LMRenderer.recolor();
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
	
	public void playSoundFX(String s, float pitch)
	{ mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(s), pitch)); }
	
	public void playClickSound()
	{ playSoundFX("gui.button.press", 1F); }

	public void drawPlayerHead(String playerName, double x, double y, double w, double h)
	{
		setTexture(texture);
		
		double z = getZLevel();
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
}