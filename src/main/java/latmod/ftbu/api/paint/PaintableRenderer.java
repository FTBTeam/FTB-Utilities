package latmod.ftbu.api.paint;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.util.client.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class PaintableRenderer
{
	public static Paint currentPaint;
	public static BlockCustom currentParentBlock;
	
	public static Paint[] to6(Paint p)
	{ return new Paint[] {p, p, p, p, p, p}; }
	
	public static void renderCube(IBlockAccess iba, RenderBlocksCustom rb, Paint[] p, BlockCustom parent, int x, int y, int z, AxisAlignedBB aabb)
	{
		for(int i = 0; i < 6; i++)
		{
			if(aabb != null) rb.setFaceBounds(i, aabb);
			else rb.setFaceBounds(i, 0D, 0D, 0D, 1D, 1D, 1D);
			renderFace(iba, rb, i, (p == null) ? null : ((p.length == 1) ? p[0] : p[i]), parent, x, y, z);
		}
		
		rb.currentSide = -1;
	}
	
	public static void renderFace(IBlockAccess iba, RenderBlocksCustom rb, int s, Paint p, BlockCustom parent, int x, int y, int z)
	{
		currentPaint = p;
		currentParentBlock = parent;
		rb.currentSide = s;
		rb.setInst(iba);
		
		int ox = x + Facing.offsetsXForSide[s];
		int oy = y + Facing.offsetsYForSide[s];
		int oz = z + Facing.offsetsZForSide[s];
		
		if(parent == null || !parent.shouldSideBeRendered(iba, ox, oy, oz, s)) return;
		
		if(iba == null) return;
		
		boolean renderAllFaces0 = rb.renderAllFaces;
		rb.renderAllFaces = true;
		
		boolean fancyGrass0 = RenderBlocks.fancyGrass;
		RenderBlocks.fancyGrass = false;
		
		if(p == null || p.block == null || p.block == Blocks.air || p.block.hasTileEntity(p.meta))
		{
			rb.setCustomColor(parent.colorMultiplier(iba, x, y, z));
			rb.clearOverrideBlockTexture();
			rb.renderStandardBlock(parent, x, y, z);
		}
		else
		{
			boolean fancyLeaves0 = (p.block instanceof BlockLeaves && p.block.isOpaqueCube());
			if(p.block instanceof BlockLeaves) ((BlockLeaves) p.block).setGraphicsLevel(false);
			
			Block sideBlock = iba.getBlock(ox, oy, oz);
			
			if(rb.renderAllFaces && !sideBlock.isAir(iba, ox, oy, oz))
			{
				double d = -0.0001D;
				if(s == 0)
				{
					rb.renderMaxY -= d;
					rb.renderMinY -= d;
				}
				if(s == 1)
				{
					rb.renderMinY += d;
					rb.renderMaxY += d;
				}
				if(s == 2)
				{
					rb.renderMaxZ -= d;
					rb.renderMinZ -= d;
				}
				if(s == 3)
				{
					rb.renderMinZ += d;
					rb.renderMaxZ += d;
				}
				if(s == 4)
				{
					rb.renderMaxX -= d;
					rb.renderMinX -= d;
				}
				if(s == 5)
				{
					rb.renderMinX += d;
					rb.renderMaxX += d;
				}
			}
			
			rb.setInst(new PaintBlockAccess(iba, x, y, z, p));
			rb.setCustomColor(p.block.colorMultiplier(rb.blockAccess, x, y, z));
			
			if(p.block == Blocks.grass && s != 1) rb.setCustomColor(1F, 1F, 1F);
			
			rb.setOverrideBlockTexture(p.getIcon(iba, rb.blockAccess, x, y, z, s));
			rb.renderStandardBlock(parent, x, y, z);
			rb.setInst(iba);
			
			if(p.block instanceof BlockLeaves) ((BlockLeaves) p.block).setGraphicsLevel(fancyLeaves0);
		}
		
		rb.renderAllFaces = renderAllFaces0;
		RenderBlocks.fancyGrass = fancyGrass0;
	}
}