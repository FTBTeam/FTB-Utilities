package latmod.ftbu.core.client;
import java.awt.Color;

import org.lwjgl.opengl.*;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.util.MathHelperLM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class LMRenderHelper
{
	private static EntityItem entityItem;
	
	public static void renderItem(World w, ItemStack is, boolean fancy, boolean frame)
	{
		if(entityItem == null) entityItem = new EntityItem(w);
		
		entityItem.worldObj = w;
		entityItem.hoverStart = 0F;
		entityItem.setEntityItemStack(is);
		
		boolean isFancy = RenderManager.instance.options.fancyGraphics;
		RenderManager.instance.options.fancyGraphics = true;
		RenderItem.renderInFrame = frame;
		
		RenderManager.instance.renderEntityWithPosYaw(entityItem, 0D, 0D, 0D, 0F, 0F);
		
		RenderManager.instance.options.fancyGraphics = isFancy;
		RenderItem.renderInFrame = false;
	}
	
	public static void enableTexture()
	{ GL11.glEnable(GL11.GL_TEXTURE_2D); }
	
	public static void disableTexture()
	{ GL11.glDisable(GL11.GL_TEXTURE_2D); }
	
	public static void setTexture(ResourceLocation rl)
	{ Minecraft.getMinecraft().getTextureManager().bindTexture(rl); }
	
	public static void drawOutlinedBoundingBoxGL(AxisAlignedBB bb)
	{
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
		GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
		GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
		GL11.glEnd();
	}

	public static int copyB(int col, int bright)
	{
		Color c0 = new Color(col, true);
		int r = MathHelperLM.clampInt(c0.getRed() + bright, 0, 255);
		int g = MathHelperLM.clampInt(c0.getGreen() + bright, 0, 255);
		int b = MathHelperLM.clampInt(c0.getBlue() + bright, 0, 255);
		return new Color(r, g, b, c0.getAlpha()).getRGB();
	}
	
	public static void renderGuiItem(ItemStack is, RenderItem itemRender, FontRenderer font, int x, int y)
	{
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		if(is == null || is.getItem() == null) return;
		GL11.glPushMatrix();
		//GL11.glTranslatef(0F, 0F, 32F);
		GL11.glEnable(GL11.GL_LIGHTING);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		FontRenderer f = is.getItem().getFontRenderer(is);
		if (f == null) f = font;
		itemRender.renderItemAndEffectIntoGUI(f, Minecraft.getMinecraft().getTextureManager(), is, x, y);
		itemRender.renderItemOverlayIntoGUI(f, LatCoreMCClient.mc.getTextureManager(), is, x, y, null);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}