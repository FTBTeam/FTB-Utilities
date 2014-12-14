package latmod.core;
import java.util.Random;

import latmod.core.mod.LC;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ParticleHelper
{
	public static final Random rand = new Random();
	
	public static void spawnFallParticles(World w, double x, double y, double z, int c, Block b, int m) // RenderGlobal // 2006
	{
		double d3 = Math.min(0.2D + (double)c / 15D, 10D);
		if (d3 > 2.5D) d3 = 2.5D;
		int l1 = (int)(150D * d3);
		for (int i2 = 0; i2 < l1; ++i2)
		{
			double f3 = MathHelper.clamp(rand.nextFloat(), 0D, MathHelper.TWO_PI);
			double d5 = MathHelper.clamp(rand.nextFloat(), 0.75D, 1D);
			double d6 = 0.20000000298023224D + d3 / 100D;
			double d7 = (double)(MathHelper.cos(f3) * 0.2F) * d5 * d5 * (d3 + 0.2D);
			double d8 = (double)(MathHelper.sin(f3) * 0.2F) * d5 * d5 * (d3 + 0.2D);
			spawnPart(w, "blockdust_" + Item.getIdFromItem(Item.getItemFromBlock(b)) + "_" + m, x, y, z, d7, d6, d8);
		}
	}
	
	public static void spawnPart(World w, String s, double x, double y, double z, double a, double b, double c)
	{ if(LC.proxy.inGameHasFocus()) w.spawnParticle(s, x, y, z, a, b, c); }
	
	public static void spawnPart(World w, String s, double x, double y, double z)
	{ spawnPart(w, s, x, y, z, 0D, 0D, 0D); }
}