package latmod.core.mod.client.render.world;
import latmod.core.client.RenderBlocksCustom;
import latmod.core.mod.block.BlockPaintable;
import latmod.core.mod.tile.TilePaintable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
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
		renderBlocks.renderAllFaces = false;
		renderBlocks.setRenderBounds(RenderBlocksCustom.FULL_BLOCK);
		renderBlocks.setCustomColor(null);
		renderBlocks.customMetadata = 0;
		renderBlocks.setOverrideBlockTexture(b.getIcon(0, 0));
		renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1F);
	}
	
	public boolean renderWorldBlock(IBlockAccess iba, int x, int y, int z, Block b, int renderID, RenderBlocks renderer0)
	{
		renderBlocks.renderAllFaces = true;
		renderBlocks.blockAccess = iba;
		renderBlocks.setRenderBounds(RenderBlocksCustom.FULL_BLOCK);
		renderBlocks.setCustomColor(null);
		
		TilePaintable t = (TilePaintable)iba.getTileEntity(x, y, z);
		TilePaintable.renderBlock(renderBlocks, t.currentPaint(), b.getIcon(0, 0), x, y, z);
		
		return true;
	}
	
	public boolean shouldRender3DInInventory(int renderID)
	{ return true; }
}