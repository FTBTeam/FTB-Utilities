package latmod.ftbu.paint;

import latmod.ftbu.util.CustomBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class PaintBlockAccess extends CustomBlockAccess
{
	public final int blockX, blockY, blockZ;
	public final Paint paint;
	
	public PaintBlockAccess(IBlockAccess iba, int x, int y, int z, Paint p)
	{ super(iba); blockX = x; blockY = y; blockZ = z; paint = p; }
	
	public Block getBlock(int x, int y, int z)
	{
		if(paint != null && x == blockX && y == blockY && z == blockZ)
			return paint.block; return super.getBlock(x, y, z);
	}
	
	public int getBlockMetadata(int x, int y, int z)
	{
		if(paint != null && x == blockX && y == blockY && z == blockZ)
			return paint.meta; return super.getBlockMetadata(x, y, z);
	}
}