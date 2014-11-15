package latmod.core.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

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
	
	public static final void colorize(int c, int a)
	{
		float r = ((c >> 16) & 255) / 255F;
		float g = ((c >> 8) & 255) / 255F;
		float b = ((c >> 0) & 255) / 255F;
		GL11.glColor4f(r, g, b, a / 255F);
	}
	
	public static final void colorize(int c)
	{ colorize(c, (c >> 24) & 255); }
	
	public static final int getColor(int c, int a)
	{
		int r = (c >> 16) & 255;
		int g = (c >> 8) & 255;
		int b = (c >> 0) & 255;
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	public static final void recolor()
	{ GL11.glColor4f(1F, 1F, 1F, 1F); }
	
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
}