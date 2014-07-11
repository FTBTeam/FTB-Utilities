package latmod.core.base.gui;
import cpw.mods.fml.relauncher.*;
import latmod.core.FastList;
import latmod.core.client.LMRenderer;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.Tessellator;
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
	
	public void drawWrappedIcon(Icon i, float x, float y, float w, float h)
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
	
	public void playSound(String s, float pitch)
	{ mc.sndManager.playSoundFX(s, 1F, pitch); }
}