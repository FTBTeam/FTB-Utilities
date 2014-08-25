package latmod.core.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LMRenderer
{
	public static RenderItem itemRenderer = new RenderItem()
	{
		public boolean shouldBob()
		{ return false; }
		
		public boolean shouldSpreadItems()
		{ return false; }
	};
	
	public static void renderItem(World w, ItemStack is, boolean fancy, boolean frame)
	{
		boolean isFancy = RenderManager.instance.options.fancyGraphics;
		RenderManager.instance.options.fancyGraphics = true;
		RenderItem.renderInFrame = frame;
		
		EntityItem ei = new EntityItem(w);
		ei.hoverStart = 0F;
		ei.setEntityItemStack(is);
		itemRenderer.setRenderManager(RenderManager.instance);
		itemRenderer.doRender(ei, 0D, 0D, 0D, 0F, 0F);
		
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
}