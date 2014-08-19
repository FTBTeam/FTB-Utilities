package latmod.core.client;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
	
	public static final void renderStandardBlockIcons(Block b, RenderBlocks r, int x, int y, int z, IIcon[] icons, boolean tess)
	{
		Tessellator tessellator = Tessellator.instance;
		//GL11.glRotatef(90F, 0F, 1F, 0F);
		
		float f = 0.5F;
		float f1 = 1.0F;
		float f2 = 0.8F;
		float f3 = 0.6F;
		
		float[] cols = { f, f1, f2, f2, f3, f3 };
		cols = new float[] { 1F, 1F, 1F, 1F, 1F, 1F };
		
		if(tess)
		{
			tessellator.startDrawingQuads();
			tessellator.setBrightness(b.getMixedBrightnessForBlock(r.blockAccess, x, y, z));
		}
		
		double off = -0.5D;
		
		for(int i = 0; i < 6; i++)
		{
			tessellator.setColorOpaque_F(cols[i], cols[i], cols[i]);
			renderFace(r, b, i, off, off, off, (icons == null || icons.length != 6) ? b.getIcon(r.blockAccess, x, y, z, i) : icons[i]);
		}
		
		if(tess) tessellator.draw();
	}
	
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

	public static void renderBlockAsItem(Block block, int metadata, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
		
		GL11.glRotatef(90F, 0F, 1F, 0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		tessellator.setColorOpaque_F(1F, 1F, 1F);
		tessellator.setBrightness(0);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		for(int i = 0; i < 6; i++)
		{
			ForgeDirection fd = ForgeDirection.VALID_DIRECTIONS[i];
			tessellator.startDrawingQuads();
			tessellator.setNormal(fd.offsetX, fd.offsetY, fd.offsetZ);
			renderFace(renderer, block, i, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, i, metadata));
			tessellator.draw();
		}
		
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	public static void renderFace(RenderBlocks r, Block b, int f, double x, double y, double z, IIcon icon)
	{
		if(f == 0) r.renderFaceYNeg(b, x, y, z, icon);
		else if(f == 1) r.renderFaceYPos(b, x, y, z, icon);
		else if(f == 2) r.renderFaceZNeg(b, x, y, z, icon);
		else if(f == 3) r.renderFaceZPos(b, x, y, z, icon);
		else if(f == 4) r.renderFaceXNeg(b, x, y, z, icon);
		else if(f == 5) r.renderFaceXPos(b, x, y, z, icon);
	}
}