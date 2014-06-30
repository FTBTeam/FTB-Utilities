package latmod.core.base;
import cpw.mods.fml.relauncher.*;
import latmod.core.client.LMRenderer;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class GuiLM extends GuiContainer
{
	public EntityPlayer player;
	public TileLM tile;
	public ResourceLocation texture;
	
	public GuiLM(ContainerLM c)
	{
		super(c);
		player = c.player;
		tile = c.tile;
		texture = c.getTexture();
	}
	
	public void setTexture(ResourceLocation tex)
	{ mc.getTextureManager().bindTexture(tex); }
	
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
	{ }
}