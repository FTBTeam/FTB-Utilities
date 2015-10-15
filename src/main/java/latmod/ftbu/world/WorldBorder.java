package latmod.ftbu.world;

import latmod.lib.MathHelperLM;
import latmod.lib.util.Pos2I;

public class WorldBorder
{
	public final int dim;
	public Pos2I pos;
	public int size;
	
	public WorldBorder(int d)
	{
		dim = d;
		pos = new Pos2I(0, 0);
		size = -1;
	}
	
	public void setPos(int x, int z)
	{ pos.set(x, z); }
	
	public boolean isOutside(int cx, int cz)
	{
		int minX = MathHelperLM.chunk(pos.x - size);
		int maxX = MathHelperLM.chunk(pos.x + size);
		int minZ = MathHelperLM.chunk(pos.y - size);
		int maxZ = MathHelperLM.chunk(pos.y + size);
		return cx >= maxX || cx <= minX || cz >= maxZ || cz <= minZ;
	}
}