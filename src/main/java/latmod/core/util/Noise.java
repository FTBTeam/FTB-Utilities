package latmod.core.util;
import java.util.*;

/** Originally made by Progressing.org
 * <br>updated by LatvianModder */
public class Noise
{
	public static final Noise def = new Noise();
	
	private double perlin_cosTable[] = null;
	private double perlin[] = null;
	private int perlin_PI;
	private int perlin_TWOPI;
	private Random perlinRandom = null;
	
	public Noise(Random r)
	{ perlinRandom = r; perlin = null; }
	
	public Noise()
	{ this(new Random()); }
	
	public double get0(double x, double y, double z)
	{
		if(perlinRandom == null) perlinRandom = new Random();
		if(perlin_cosTable == null)
		{
			perlin_cosTable = new double[720];
			for (int i = 0; i < 720; i++)
			perlin_cosTable[i] = (float)Math.cos(i * 0.01745329F * 0.5F);
		}
		if (perlin == null)
		{
			if (perlinRandom == null) perlinRandom = new Random();
			perlin = new double[4096];
			for (int i = 0; i < perlin.length; i++)
				perlin[i] = perlinRandom.nextFloat();
			perlin_TWOPI = (perlin_PI = 720);
			perlin_PI >>= 1;
		}
		
		if (x < 0D) x = -x;
		if (y < 0D) y = -y;
		if (z < 0D) z = -z;
		
		int i = (int)x;
		int j = (int)y;
		int k = (int)z;
		
		double f1 = x - i;
		double f2 = y - j;
		double f3 = z - k;
		
		float f6 = 0.0F;
		float f7 = 0.5F;
		
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
			i <<= 1;
		  	f1 *= 2.0F;
		  	j <<= 1;
		  	f2 *= 2.0F;
		  	k <<= 1;
		  	f3 *= 2.0F;
		  	
		  	if (f1 >= 1.0F)
		  	{
		  		i++;
		  		f1 -= 1.0F;
		  	}
		  	
		  	if (f2 >= 1.0F)
		  	{
		  		j++;
		  		f2 -= 1.0F;
		  	}
		  	
		  	if (f3 < 1.0F) continue;
		  	k++;
		  	f3 -= 1.0F;
		}
		
		return f6;
	}
	
	public double get(double... pos)
	{ return get0(pos[0], (pos.length >= 2) ? pos[1] : 0D, (pos.length >= 3) ? pos[2] : 0D); }
	
	private double noise_fsc(double f)
	{ return 0.5D * (1D - perlin_cosTable[((int)(f * perlin_PI) % perlin_TWOPI)]); }
}