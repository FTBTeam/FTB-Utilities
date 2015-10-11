package latmod.ftbu.world;

import latmod.ftbu.util.*;
import latmod.lib.MathHelperLM;
import net.minecraft.util.ChunkCoordinates;

public class WorldBorder
{
	public final int dim;
	public boolean enabled;
	public int posX, posZ, size;
	
	public WorldBorder(int d)
	{
		dim = d;
		enabled = false;
		posX = posZ = 0;
		size = -1;
	}
	
	public void setPos(int x, int z)
	{ posX = x; posZ = z; }
	
	public boolean isOutside(int cx, int cz)
	{
		int minX = MathHelperLM.chunk(posX - size);
		int maxX = MathHelperLM.chunk(posX + size);
		int minZ = MathHelperLM.chunk(posZ - size);
		int maxZ = MathHelperLM.chunk(posZ + size);
		return cx >= maxX || cx <= minX || cz >= maxZ || cz <= minZ;
	}
	
	// Static //
	
	public static boolean isInSpawn(int dim, int cx, int cz)
	{
		if(dim != 0) return false;
		int radius = LatCoreMC.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		ChunkCoordinates c = LMDimUtils.getSpawnPoint(0);
		int minX = MathHelperLM.chunk(c.posX + 0.5D - radius);
		int minZ = MathHelperLM.chunk(c.posZ + 0.5D - radius);
		int maxX = MathHelperLM.chunk(c.posX + 0.5D + radius);
		int maxZ = MathHelperLM.chunk(c.posZ + 0.5D + radius);
		return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
	}
	
	public static boolean isInSpawnF(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
}