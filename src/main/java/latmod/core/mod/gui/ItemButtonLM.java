package latmod.core.mod.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.*;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class ItemButtonLM extends ButtonLM
{
	public ItemStack item = null;
	public TextureCoords background = null;
	
	private static final RenderItem itemRender = new RenderItem();
	
	public ItemButtonLM(GuiLM g, int x, int y, int w, int h)
	{ super(g, x, y, w, h); }
	
	public void setItem(ItemStack is)
	{ item = is; }
	
	public void render(TextureCoords c, double rw, double rh)
	{
		rw = rh = 1D;
		
		if(background != null)
			super.render(background, rw, rh);
		
		if(item != null)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0F, 0F, 32F);
			GL11.glScaled(rw, rh, 0D);
			GL11.glEnable(GL11.GL_LIGHTING);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			gui.setZLevel(200F);
			itemRender.zLevel = 200F;
			FontRenderer font = item.getItem().getFontRenderer(item);
			if (font == null) font = gui.getFontRenderer();
			
			int x = gui.getPosX() + c.posX;
			int y = gui.getPosY() + c.posY;
			
			itemRender.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), item, x, y);
			//itemRender.renderItemOverlayIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), item, x, y, "Test");
			gui.setZLevel(0F);
			itemRender.zLevel = 0F;
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
		
		//GuiContainer
		//gui.setTexture(c.texture);
		//gui.drawTexturedModalRect(gui.getPosX() + posX, gui.getPosY() + posY, c.posX, c.posY, (int)(width * rw), (int)(height * rh));
	}
}