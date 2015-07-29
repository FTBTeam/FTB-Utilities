package latmod.ftbu.core.paint;

import net.minecraft.world.IBlockAccess;

public interface INoPaintBlock
{
	public boolean hasPaint(IBlockAccess iba, int x, int y, int z, int s);
}