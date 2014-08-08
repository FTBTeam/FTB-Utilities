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
	
	public static final void recolor()
	{ GL11.glColor4f(1F, 1F, 1F, 1F); }
	
	public static final void renderStandardBlockIcons(Block b, RenderBlocks r, int x, int y, int z, IIcon[] icons)
	{
		if(icons == null || icons.length != 6) return;
		
		Tessellator tessellator = Tessellator.instance;
		//GL11.glRotatef(90F, 0F, 1F, 0F);
		
		float f = 0.5F;
		float f1 = 1.0F;
		float f2 = 0.8F;
		float f3 = 0.6F;
		
		tessellator.startDrawingQuads();
		tessellator.setBrightness(b.getMixedBrightnessForBlock(r.blockAccess, x, y, z));
		tessellator.setColorOpaque_F(f, f, f);
		double off = -0.5D;
		r.renderFaceYNeg(b, off, off, off, icons[0]);
		tessellator.setColorOpaque_F(f1, f1, f1);
		r.renderFaceYPos(b, off, off, off, icons[1]);
		tessellator.setColorOpaque_F(f2, f2, f2);
		r.renderFaceZNeg(b, off, off, off, icons[2]);
		tessellator.setColorOpaque_F(f2, f2, f2);
		r.renderFaceZPos(b, off, off, off, icons[3]);
		tessellator.setColorOpaque_F(f3, f3, f3);
		r.renderFaceXNeg(b, off, off, off, icons[4]);
		tessellator.setColorOpaque_F(f3, f3, f3);
		r.renderFaceXPos(b, off, off, off, icons[5]);
		tessellator.draw();
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

	public static void enableTexture()
	{ GL11.glEnable(GL11.GL_TEXTURE_2D); }
	
	public static void disableTexture()
	{ GL11.glDisable(GL11.GL_TEXTURE_2D); }
	
	public static void setTexture(ResourceLocation rl)
	{ Minecraft.getMinecraft().getTextureManager().bindTexture(rl); }

	public static void renderBlockAsItem(Block block, int metadata, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
        GL11.glRotatef(90F, 0F, 1F, 0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0F, -1F, 0F);
        renderer.renderFaceYNeg(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0F, 1F, 0F);
        renderer.renderFaceYPos(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0F, 0F, -1F);
        renderer.renderFaceZNeg(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0F, 0F, 1F);
        renderer.renderFaceZPos(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0F, 0F);
        renderer.renderFaceXNeg(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1F, 0F, 0F);
        renderer.renderFaceXPos(block, 0D, 0D, 0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
}