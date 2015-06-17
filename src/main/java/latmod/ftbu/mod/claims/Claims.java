package latmod.ftbu.mod.claims;

import latmod.ftbu.core.util.MathHelperLM;

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
		return false;
	}
	
	public static boolean isInSpawn(int dim, double x, double z)
	{ return isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public static boolean isOutsideWorldBorder(int dim, int cx, int cz)
	{
		return false;
	}
	
	public static boolean isOutsideWorldBorderD(int dim, double x, double z)
	{ return isOutsideWorldBorder(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
}