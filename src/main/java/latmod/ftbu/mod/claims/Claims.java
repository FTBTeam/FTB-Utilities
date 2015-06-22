package latmod.ftbu.mod.claims;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.MathHelperLM;
import latmod.ftbu.mod.FTBUConfig;
import net.minecraft.util.ChunkCoordinates;

public class Claims
{
	public static void init()
	{
	}
	
	public static ClaimedChunk get(int dim, int cx, int cz)
	{
		return null;
	}
	
	public static boolean isInSpawn(int dim, int cx, int cz)
	{
		if(dim != 0) return false;
		int radius = LatCoreMC.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		ChunkCoordinates c = LatCoreMC.getSpawnPoint(0);
		double minX = MathHelperLM.chunk(c.posX + 0.5D - radius);
		double minZ = MathHelperLM.chunk(c.posZ + 0.5D - radius);
		double maxX = MathHelperLM.chunk(c.posX + 0.5D + radius);
		double maxZ = MathHelperLM.chunk(c.posZ + 0.5D + radius);
		return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
	}
	
	public static boolean isInSpawn(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public static boolean isOutsideWorldBorder(int dim, int cx, int cz)
	{
		if(!FTBUConfig.WorldBorder.enabled) return false;
		int radius = FTBUConfig.WorldBorder.getWorldBorder(dim);
		int min = MathHelperLM.chunk(-radius);
		int max = MathHelperLM.chunk(radius);
		return cx >= max || cx <= min || cz >= max || cz <= min;
	}
	
	public static boolean isOutsideWorldBorderD(int dim, double x, double z)
	{ return isOutsideWorldBorder(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
}