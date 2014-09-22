package latmod.core.mod.client.render.block;
import latmod.core.client.RenderBlocksCustom;
import latmod.core.mod.LCItems;
import latmod.core.mod.block.BlockPaintable;
import latmod.core.mod.tile.TilePaintable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class RenderPaintable implements ISimpleBlockRenderingHandler
{
	public RenderBlocksCustom renderBlocks = new RenderBlocksCustom();
	
	public int getRenderId()
	{ return BlockPaintable.renderID; }
	
	public void renderInventoryBlock(Block b, int paramInt1, int paramInt2, RenderBlocks renderer)
	{
		renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
		renderBlocks.setCustomColor(null);
		renderBlocks.customMetadata = 0;
		renderBlocks.setOverrideBlockTexture(LCItems.b_paintable.getBlockIcon());
		renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1F);
	}
	
	public boolean renderWorldBlock(IBlockAccess iba, int x, int y, int z, Block b, int renderID, RenderBlocks renderer0)
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		renderBlocks.blockAccess = iba;
		renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
		renderBlocks.setCustomColor(null);
		
		TilePaintable t = (TilePaintable)iba.getTileEntity(x, y, z);
		
		renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 0D, 1D);
		renderBlocks.setOverrideBlockTexture(t.getIcon(ForgeDirection.DOWN));
		renderBlocks.renderStandardBlock(Blocks.stone, x, y, z);
		
		renderBlocks.setRenderBounds(0D, 1D, 0D, 1D, 1D, 1D);
		renderBlocks.setOverrideBlockTexture(t.getIcon(ForgeDirection.UP));
		renderBlocks.renderStandardBlock(Blocks.stone, x, y, z);
		
		renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 1D, 0D);
		renderBlocks.setOverrideBlockTexture(t.getIcon(ForgeDirection.NORTH));
		renderBlocks.renderStandardBlock(Blocks.stone, x, y, z);
		
		renderBlocks.setRenderBounds(0D, 0D, 1D, 1D, 1D, 1D);
		renderBlocks.setOverrideBlockTexture(t.getIcon(ForgeDirection.SOUTH));
		renderBlocks.renderStandardBlock(Blocks.stone, x, y, z);
		
		renderBlocks.setRenderBounds(0D, 0D, 0D, 0D, 1D, 1D);
		renderBlocks.setOverrideBlockTexture(t.getIcon(ForgeDirection.WEST));
		renderBlocks.renderStandardBlock(Blocks.stone, x, y, z);
		
		renderBlocks.setRenderBounds(1D, 0D, 0D, 1D, 1D, 1D);
		renderBlocks.setOverrideBlockTexture(t.getIcon(ForgeDirection.EAST));
		renderBlocks.renderStandardBlock(Blocks.stone, x, y, z);
		
		/*
		
		
		for(int s = 0; s < 6; s++)
		{
			Block bl = LCItems.b_paintable;
			renderBlocks.customMetadata = 0;
			
			if(t.paintItems[s] != null)
			{
				bl = Block.getBlockFromItem(t.paintItems[s].getItem());
				renderBlocks.customMetadata = t.paintItems[s].getItemDamage();
				
				renderBlocks.setCustomColor(bl.getRenderColor(renderBlocks.customMetadata));
			}
			
			else renderBlocks.setOverrideBlockTexture(LCItems.b_paintable.getBlockIcon());
			
			renderBlocks.updateColor();
			renderBlocks.renderFace(Blocks.stone, s, x, y, z, bl.getIcon(iba, x, y, z, s));
			
			//renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
			//renderBlocks.renderStandardBlock(Blocks.stone, x, y, z);
		}*/
		
		return true;
	}
	
	public boolean shouldRender3DInInventory(int renderID)
	{ return true; }
}