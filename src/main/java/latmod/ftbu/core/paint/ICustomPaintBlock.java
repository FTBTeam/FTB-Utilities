package latmod.ftbu.core.paint;

import net.minecraft.world.World;

public interface ICustomPaintBlock
{
	public Paint getCustomPaint(World w, int x, int y, int z);
}