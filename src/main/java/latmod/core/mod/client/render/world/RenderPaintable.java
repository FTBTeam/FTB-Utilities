package latmod.core.mod.client.render.world;
import latmod.core.client.RenderBlocksCustom;
import latmod.core.mod.block.BlockPaintable;
import latmod.core.mod.tile.TilePaintable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
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
		renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
		renderBlocks.setCustomColor(null);
		renderBlocks.customMetadata = 0;
		renderBlocks.setOverrideBlockTexture(b.getIcon(0, 0));
		renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1F);
	}
	
	public boolean renderWorldBlock(IBlockAccess iba, int x, int y, int z, Block b, int renderID, RenderBlocks renderer0)
	{
		renderBlocks.renderAllFaces = true;
		renderBlocks.blockAccess = iba;
		renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
		renderBlocks.setCustomColor(null);
		
		TilePaintable t = (TilePaintable)iba.getTileEntity(x, y, z);
		
		double d0 = 0D;
		double d1 = 1D - d0;
		
		renderBlocks.setRenderBounds(0D, d0, 0D, 1D, d0, 1D);
		t.renderFace(renderBlocks, ForgeDirection.DOWN);
		
		renderBlocks.setRenderBounds(0D, d1, 0D, 1D, d1, 1D);
		t.renderFace(renderBlocks, ForgeDirection.UP);
		
		renderBlocks.setRenderBounds(0D, 0D, d0, 1D, 1D, d0);
		t.renderFace(renderBlocks, ForgeDirection.NORTH);
		
		renderBlocks.setRenderBounds(0D, 0D, d1, 1D, 1D, d1);
		t.renderFace(renderBlocks, ForgeDirection.SOUTH);
		
		renderBlocks.setRenderBounds(d0, 0D, 0D, d0, 1D, 1D);
		t.renderFace(renderBlocks, ForgeDirection.WEST);
		
		renderBlocks.setRenderBounds(d1, 0D, 0D, d1, 1D, 1D);
		t.renderFace(renderBlocks, ForgeDirection.EAST);
		
		return true;
	}
	
	public boolean shouldRender3DInInventory(int renderID)
	{ return true; }
}