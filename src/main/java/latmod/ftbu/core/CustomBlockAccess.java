package latmod.ftbu.core;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.*;

public class CustomBlockAccess implements IBlockAccess
{
	public IBlockAccess parent;
	
	public CustomBlockAccess(IBlockAccess iba)
	{
		if(iba instanceof CustomBlockAccess)
			parent = ((CustomBlockAccess)iba).parent;
		else parent = iba;
	}
	
	public Block getBlock(int x, int y, int z)
	{ return parent.getBlock(x, y, z); }
	
	public int getBlockMetadata(int x, int y, int z)
	{ return parent.getBlockMetadata(x, y, z); }
	
	public TileEntity getTileEntity(int x, int y, int z)
	{ return parent.getTileEntity(x, y, z); }

	@SideOnly(Side.CLIENT)
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int b)
	{ return parent.getLightBrightnessForSkyBlocks(x, y, z, b); }
	
	public int isBlockProvidingPowerTo(int x, int y, int z, int s)
	{ return parent.isBlockProvidingPowerTo(x, y, z, s); }
	
	public boolean isAirBlock(int x, int y, int z)
	{ return parent.isAirBlock(x, y, z); }
	
	@SideOnly(Side.CLIENT)
	public BiomeGenBase getBiomeGenForCoords(int x, int z)
	{ return parent.getBiomeGenForCoords(x, z); }
	
	@SideOnly(Side.CLIENT)
	public int getHeight()
	{ return parent.getHeight(); }
	
	@SideOnly(Side.CLIENT)
	public boolean extendedLevelsInChunkCache()
	{ return parent.extendedLevelsInChunkCache(); }
	
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default)
	{ return parent.isSideSolid(x, y, z, side, _default); }
}