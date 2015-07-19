package latmod.ftbu.core.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.*;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class ItemButtonLM extends ButtonLM
{
	public ItemStack item = null;
	
	private static final RenderItem itemRender = new RenderItem();
	
	public ItemButtonLM(GuiLM g, int x, int y, int w, int h)
	{ super(g, x, y, w, h); }
	
	public ItemButtonLM setItem(ItemStack is)
	{ item = is; return this; }
	
	public void setBackground(TextureCoords t)
	{ background = t; }
	
	public void render()
	{
		if(item != null)
		{
			gui.setTexture(TextureMap.locationItemsTexture);
			
			GL11.glPushMatrix();
			GL11.glTranslatef(0F, 0F, 32F);
			GL11.glEnable(GL11.GL_LIGHTING);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			gui.setZLevel(200F);
			itemRender.zLevel = 200F;
			FontRenderer font = item.getItem().getFontRenderer(item);
			if (font == null) font = gui.getFontRenderer();
			
			int x = gui.getPosX() + posX;
			int y = gui.getPosY() + posY;
			
			itemRender.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), item, x, y);
			gui.setZLevel(0F);
			itemRender.zLevel = 0F;
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}
}