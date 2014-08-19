package latmod.core.mod.client.render.block;
import latmod.core.mod.LCItems;
import latmod.core.mod.block.BlockPaintable;
import latmod.core.mod.tile.IPaintable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class RenderPaintable implements ISimpleBlockRenderingHandler
{
	public RenderBlocks renderBlocks = new RenderBlocks();
	
	public int getRenderId()
	{ return BlockPaintable.renderID; }
	
	public void renderInventoryBlock(Block b, int paramInt1, int paramInt2, RenderBlocks renderer)
	{
		renderBlocks.setOverrideBlockTexture(LCItems.b_paintable.getBlockTextureFromSide(0));
		renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1F);
	}
	
	public boolean renderWorldBlock(IBlockAccess iba, int x, int y, int z, Block b, int renderID, RenderBlocks renderer0)
	{
		renderBlocks.blockAccess = iba;
		
		IPaintable t = (IPaintable)iba.getTileEntity(x, y, z);
		
		ItemStack is = t.getPaint();
		
		if(is != null)
		{
			Block paintBlock = Block.getBlockFromItem(is.getItem());
			renderBlocks.setOverrideBlockTexture(paintBlock.getIcon(1, is.getItemDamage()));
		}
		
		else renderBlocks.setOverrideBlockTexture(LCItems.b_paintable.getBlockTextureFromSide(0));
		
		renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
		renderBlocks.renderStandardBlock(Blocks.stone, x, y, z);
		
		return true;
	}
	
	public boolean shouldRender3DInInventory(int renderID)
	{ return true; }
}