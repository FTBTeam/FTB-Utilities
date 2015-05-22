package latmod.core.client;

import latmod.core.LatCoreMC;
import latmod.core.mod.LCConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class BlockRendererLM implements ISimpleBlockRenderingHandler
{
	private final int renderID = LatCoreMC.Client.getNewBlockRenderID();
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
	{ LatCoreMC.Client.addBlockRenderer(renderID, this); }
	
	public final void registerItemRenderer(Block b)
	{ if(this instanceof IItemRenderer) LatCoreMC.Client.addItemRenderer(b, (IItemRenderer)this); }
	
	public static void rotateBlocks()
	{
		if(LCConfig.Client.rotateBlocks)
			GL11.glRotated(Minecraft.getSystemTime() * 0.053D, 0D, 1D, 0D);
	}
	
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
		public static final int MAX = 0xF000F0;
		
		public BlockGlowing()
		{ setLightLevel(1F); }
		
		public int getMixedBrightnessForBlock(IBlockAccess iba, int x, int y, int z)
		{ return MAX; }
	}
}