package latmod.core.util;
import java.util.Random;

/** Originally made by Progressing.org
 * <br>updated by LatvianModder */
public class Noise
{
	public static final Noise def = new Noise();
	
	private static final double perlin_cosTable[] = new double[720];
	
	static
	{
		for(int i = 0; i < perlin_cosTable.length; i++)
			perlin_cosTable[i] = Math.cos(i * 0.5D * MathHelperLM.RAD);
	}
	
	private final Random perlinRandom;
	private final double perlin[] = new double[4096];
	private int perlin_PI;
	private int perlin_TWOPI;
	
	public Noise(Random r)
	{
		perlinRandom = (r == null ? new Random() : r);
		
		for (int i = 0; i < perlin.length; i++)
			perlin[i] = perlinRandom.nextDouble();
		perlin_TWOPI = (perlin_PI = 720);
		perlin_PI >>= 1;
	}
	
	public Noise()
	{ this(null); }
	
	public double get(double... pos)
	{ return get0(pos[0], (pos.length >= 2) ? pos[1] : 0D, (pos.length >= 3) ? pos[2] : 0D); }
	
	private double get0(double x, double y, double z)
	{
		int i = (int) Math.abs(x);
		int j = (int) Math.abs(y);
		int k = (int) Math.abs(z);
		
		double f1 = x - i;
		double f2 = y - j;
		double f3 = z - k;
		
		double f6 = 0D;
		double f7 = 0.5D;
		
		for (int m = 0; m < 4; m++)
		{
			int n = i + (j << 4) + (k << 8);
			double f4 = noise_fsc(f1);
			double f5 = noise_fsc(f2);
			
			double f8 = perlin[(n & 0xFFF)];
			f8 += f4 * (perlin[(n + 1 & 0xFFF)] - f8);
			
			double f9 = perlin[(n + 16 & 0xFFF)];
			f9 += f4 * (perlin[(n + 16 + 1 & 0xFFF)] - f9);
			f8 += f5 * (f9 - f8);
			n += 256;
			
			f9 = perlin[(n & 0xFFF)];
			f9 += f4 * (perlin[(n + 1 & 0xFFF)] - f9);
			
			double f10 = perlin[(n + 16 & 0xFFF)];
			f10 += f4 * (perlin[(n + 16 + 1 & 0xFFF)] - f10);
			
			f9 += f5 * (f10 - f9);
			f8 += noise_fsc(f3) * (f9 - f8);
			f6 += f8 * f7;
			
			f7 *= 0.5F;
		  	
		  	i <<= 1; f1 *= 2D; if (f1 >= 1D) { i++; f1 -= 1D; }
		  	j <<= 1; f2 *= 2D; if (f2 >= 1D) { j++; f2 -= 1D; }
		  	k <<= 1; f3 *= 2D; if (f3 >= 1D) { k++; f3 -= 1D; }
		}
		
		return f6;
	}
	
	private double noise_fsc(double f)
	{ return 0.5D * (1D - perlin_cosTable[((int)(f * perlin_PI) % perlin_TWOPI)]); }
}