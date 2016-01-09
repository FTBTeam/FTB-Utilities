package latmod.ftbu.util.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

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
	
	public final void registerItemRenderer(Block b)
	{ if(this instanceof IItemRenderer) LatCoreMCClient.addItemRenderer(b, (IItemRenderer) this); }
}