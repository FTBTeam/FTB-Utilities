package latmod.core.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class BlockRendererLM implements ISimpleBlockRenderingHandler
{
	private final int renderID = LatCoreMCClient.getNewBlockRenderID();
	public RenderBlocksCustom renderBlocks = new RenderBlocksCustom();
	
	public void renderInventoryBlock(Block b, int meta, int modelID, RenderBlocks rb)
	{
		renderBlocks.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
		renderBlocks.clearOverrideBlockTexture();
		renderBlocks.renderBlockAsItem(b, 0, 1F);
	}
	
	public boolean renderWorldBlock(IBlockAccess iba, int x, int y, int z, Block b, int modelID, RenderBlocks rb)
	{
		return false;
	}
	
	public boolean shouldRender3DInInventory(int modelId)
	{ return true; }
	
	public final int getRenderId()
	{ return renderID; }
	
	public final void register()
	{ LatCoreMCClient.addBlockRenderer(renderID, this); }
	
	public static class BlockCustom extends Block
	{
		public BlockCustom()
		{ super(Material.glass); }
		
		public boolean isOpaqueCube()
		{ return false; }
		
		public boolean renderAsNormalBlock()
		{ return false; }
	};
	
	public static class BlockGlowing extends BlockCustom
	{
		public BlockGlowing()
		{ setLightLevel(1F); }
		
		public int getMixedBrightnessForBlock(IBlockAccess iba, int x, int y, int z)
		{ return 15 << 20 | 15 << 4; }
	}
}