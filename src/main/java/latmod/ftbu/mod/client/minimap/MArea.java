package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.util.FastMap;

public class MArea
{
	public final int dim;
	public final Pos2D pos;
	public final FastMap<Pos2D, MChunk> chunks;
	public int textureID = -1;
	
	public MArea(int d, Pos2D c)
	{
		dim = d;
		pos = c;
		chunks = new FastMap<Pos2D, MChunk>();
	}
	
	public static Pos2D getPos(int cx, int cz)
	{
		int x = cx / 128;
		int z = cz / 128;
		if(cx < 0) x -= 1;
		if(cz < 0) z -= 1;
		return new Pos2D(x, z);
	}
}