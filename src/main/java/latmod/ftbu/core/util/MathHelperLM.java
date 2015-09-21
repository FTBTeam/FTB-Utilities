package latmod.ftbu.core.util;
import java.util.Random;

/** Made by LatvianModder */
public class MathHelperLM
{
	public static final Random rand = new Random();
	public static final double RAD = Math.PI / 180D;
	public static final double DEG = 180D / Math.PI;
	public static final double TWO_PI = Math.PI * 2D;
	public static final double HALF_PI = Math.PI / 2D;
	
	public static final float RAD_F = (float)RAD;
	public static final float DEG_F = (float)DEG;
	
	private static final int SIN_TABLE_SIZE = 65536;
	private static final double[] SIN_TABLE = new double[SIN_TABLE_SIZE];
	private static final double SIN_SCALE = SIN_TABLE_SIZE / TWO_PI;
	private static final double COS_SHIFT = SIN_TABLE_SIZE / 4D;
	
	static
	{
		double ds = TWO_PI / (double)SIN_TABLE_SIZE;
		for(int i = 0; i < SIN_TABLE_SIZE; i++)
			SIN_TABLE[i] = Math.sin(i * ds);
	}
	
	public static double sin(double d)
	{ return SIN_TABLE[(int)(d * SIN_SCALE) & SIN_TABLE_SIZE]; }
	
	public static double cos(double d)
	{ return SIN_TABLE[(int)(d * SIN_SCALE + COS_SHIFT) & SIN_TABLE_SIZE]; }
	
	public static double tan(double d)
	{ return sin(d) / cos(d); }
	
	public static double sinFromDeg(double f)
	{ return sin(f * RAD); }
	
	public static double cosFromDeg(double f)
	{ return cos(f * RAD); }
	
	public static double tanFromDeg(double f)
	{ return tan(f * RAD); }
	
	public static double sqrt(double d)
	{
		if(d == 0D) return 0D;
		else if(d == 1D) return 1D;
		else return Math.sqrt(d);
	}
	
	public static double sqrt2sq(double x, double y)
	{ return sqrt(sq(x) + sq(y)); }
	
	public static double sqrt3sq(double x, double y, double z)
	{ return sqrt(sq(x) + sq(y) + sq(z)); }
	
	public static double sq(double f)
	{ return f * f; }
	
	public static double power(double i, int n)
	{
		if(n == 2) return i * i;
		double i1 = 1D;
		for(int j = 0; j < n; j++)
			i1 *= i;
		return i1;
	}
	
	public static int power(int i, int n)
	{
		if(n == 2) return i * i;
		int i1 = 1;
		for(int j = 0; j < n; j++)
			i1 *= i;
		return i1;
	}
	
	public static long power(long i, int n)
	{
		if(n == 2) return i * i;
		long i1 = 1L;
		for(int j = 0; j < n; j++)
			i1 *= i;
		return i1;
	}
	
	public static double distSq(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		if(x1 == x2 && y1 == y2 && z1 == z2) return 0D;
		return (sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1));
	}
	
	public static double dist(double x1, double y1, double z1, double x2, double y2, double z2)
	{ return sqrt(distSq(x1, y1, z1, x2, y2, z2)); }
	
	public static double distSq(double x1, double y1, double x2, double y2)
	{
		if(x1 == x2 && y1 == y2) return 0D;
		return (sq(x2 - x1) + sq(y2 - y1));
	}
	
	public static double dist(double x1, double y1, double x2, double y2)
	{ return sqrt(distSq(x1, y1, x2, y2)); }
	
	public static VecLM getLook(VecLM v, double yaw, double pitch, double dist)
	{
		if(v == null) v = new VecLM();
		double f = cos(pitch * RAD);
		double x1 = cos(-yaw * RAD + HALF_PI);
        double z1 = sin(-yaw * RAD + HALF_PI);
        double y1 = sin(pitch * RAD);
        v.set(x1 * f * dist, y1 * dist, z1 * f * dist);
        return v;
	}
	
	public static int floor(double d)
	{ int i = (int)d; return d < (double)i ? i - 1 : i; }
	
	public static int ceil(double d)
	{ int i = (int)d; return d > (double)i ? i + 1 : i; }
	
	public static int chunk(int i)
	{ return i >> 4; }
	
	public static int chunk(double d)
	{ return chunk(floor(d)); }
	
	public static int randomInt(Random r, int min, int max)
	{
		if(min == max) return min;
		if(min > max)
		{
			int min0 = min;
			min = max;
			max = min0; 
		}
		return min + r.nextInt(max - min + 1);
	}
	
	public static double randomDouble(Random r, double min, double max)
	{
		if(min == max) return min;
		if(min > max)
		{
			double min0 = min;
			min = max;
			max = min0; 
		}
		return min + r.nextDouble() * (max - min);
	}
	
	public static boolean isRound(double d)
	{ return Math.round(d) == d; }
	
	public static int lerpInt(int i1, int i2, double f)
	{ return i1 + (int)((i2 - i1) * f); }
	
	public static double lerp(double f1, double f2, double f)
	{ return f1 + (f2 - f1) * f; }
	
	public static double clamp(double n, double min, double max)
	{ if(n < min) return min; if(n > max) return max; return n; }
	
	public static int clampInt(int n, int min, int max)
	{ if(n < min) return min; if(n > max) return max; return n; }
	
	public static float clampFloat(float n, float min, float max)
	{ if(n < min) return min; if(n > max) return max; return n; }
	
	public static double[] clamp(double[] d, double min, double max)
	{ for(int i = 0; i < d.length; i++) d[i] = clamp(d[i], min, max); return d; }
	
	public static int[] clampInt(int[] i, int min, int max)
	{ for(int j = 0; j < i.length; j++) i[j] = clampInt(i[j], min, max); return i; }
	
	public static double toSmallDouble(double d)
	{ long i = (long)(d * 100D); return i / 100D; }
	
	public static float toSmallFloat(float d)
	{ long i = (int)(d * 100F); return i / 100F; }
	
	public static Integer decode(String s)
	{
		try { Integer i = Integer.decode(s); return i; }
		catch(Exception e) { } return null;
	}
	
	public static boolean canParseInt(String s)
	{
		try { Integer.parseInt(s); return true; }
		catch(Exception e) { } return false;
	}
	
	public static double map(double val, double min1, double max1, double min2, double max2)
	{ return min2 + (max2 - min2) * ((val - min1) / (max1 - min1)); }
	
	public static int mapInt(int val, int min1, int max1, int min2, int max2)
	{ return min2 + (max2 - min2) * ((val - min1) / (max1 - min1)); }
	
	public static final VecLM getMidPoint(double x1, double y1, double z1, double x2, double y2, double z2, double p)
	{ double x = x2 - x1; double y = y2 - y1; double z = z2 - z1; double d = Math.sqrt(x * x + y * y + z * z);
	return new VecLM(x1 + (x / d) * (d * p), y1 + (y / d) * (d * p), z1 + (z / d) * (d * p)); }
	
	public static VecLM getMidPoint(VecLM v1, VecLM v2, double p)
	{ return getMidPoint(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, p); }
	
	public static String formatDouble(double d)
	{ String s = String.valueOf(d); if(s.endsWith(".0"))
	return s.substring(0, s.length() - 2); return s; }
	
	public static final int getRotations(double yaw, int max)
	{ return floor((yaw * max / 360D) + 0.5D) & (max - 1); }
	
	public static final int getRotYaw(int rot)
	{
		if(rot == 2) return 180;
		else if(rot == 3) return 0;
		else if(rot == 4) return 90;
		else if(rot == 5) return -90;
		return 0;
	}
	
	public static final int getRotPitch(int rot)
	{
		if(rot == 0) return 90;
		else if(rot == 1) return -90;
		return 0;
	}
	
	public static boolean inRange(double d, double min, double max)
	{ return d >= min && d <= max; }
	
	public static int percent(double d, double max)
	{ return (int)(d / max * 100D); }
	
	public static Number min(Number... v)
	{
		if(v == null || v.length == 0) return 0;
		Number m = v[0];
		
		for(int i = 0; i < v.length; i++)
			if(v[i].doubleValue() < m.doubleValue()) m = v[i];
		
		return m;
	}
	
	public static Number max(Number... v)
	{
		if(v == null || v.length == 0) return 0;
		Number m = v[0];
		
		for(int i = 0; i < v.length; i++)
			if(v[i].doubleValue() > m.doubleValue()) m = v[i];
		
		return m;
	}
	
	public static final int[] flip(int[] i)
	{
		if(i == null) return null;
		int ai[] = new int[i.length];
		for(int j = 0; j < i.length; j++)
			ai[j] = i[(i.length - 1) - j];
		return ai;
	}
	
	public static final int[] getAllInts(int min, int size)
	{
		int[] ai = new int[size];
		for(int i = 0; i < size; i++)
			ai[i] = min + i;
		return ai;
	}
	
	public static double wrap(double i, double n)
	{ i = i % n; if(i < 0) i += n; return i; }
	
	public static int wrap(int i, int n)
	{ i = i % n; if(i < 0) i += n; return i; }
	
	public static boolean isPow2(int i)
	{ return i != 0 && (i & i - 1) == 0; }
	
	// MathHelper
}