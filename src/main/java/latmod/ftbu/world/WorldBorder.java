package latmod.ftbu.world;

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
		size = 0;
	}
}