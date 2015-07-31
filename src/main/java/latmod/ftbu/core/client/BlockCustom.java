package latmod.ftbu.core.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockCustom extends Block
{
	public static final int MAX_BRIGHTNESS = 0xF000F0;
	
	public BlockCustom()
	{ super(Material.glass); }
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
	
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s)
	{ return RenderBlocksCustom.inst.currentSide == -1 || s == RenderBlocksCustom.inst.currentSide; }
	
	public int getLightValue()
	{ return 0; }
	
	public int getMixedBrightnessForBlock(IBlockAccess iba, int x, int y, int z)
	{
		int i = getLightValue(); if(i >= 15) return MAX_BRIGHTNESS;
		else return iba.getLightBrightnessForSkyBlocks(x, y, z, Math.max(0, i));
	}
}
