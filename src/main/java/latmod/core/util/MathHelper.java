package latmod.core.util;
import java.util.Random;

/** Made by LatvianModder */
public class MathHelper // Converter
{
	public static final Random rand = new Random();
	public static final double RAD = Math.PI / 180D;
	public static final double DEG = 180D / Math.PI;
	public static final double TWO_PI = Math.PI * 2D;
	public static final double HALF_PI = Math.PI / 2D;
	
	public static double sin(double d)
	{ return Math.sin(d); }
	
	public static double cos(double d)
	{ return Math.cos(d); }
	
	public static double tan(double d)
	{ return Math.tan(d); }
	
	/** atan2 using Vertex */
	public static double atan2(Vertex pos, Vertex tar)
	{ if(pos == null || tar == null) return -1F;
	return Math.atan2(pos.x - tar.x, pos.z - tar.z); }
	
	/** atan using Vertex */
	public static double atan(Vertex pos, Vertex tar)
	{ if(pos == null || tar == null) return -1F;
	return Math.atan(pos.y - tar.y); }
	
	public static double sqrt(double f)
	{ return Math.sqrt(f); }
	
	public static double sqrt2sq(double x, double y)
	{ return sqrt(sq(x) + sq(y)); }
	
	public static double sqrt3sq(double x, double y, double z)
	{ return sqrt(sq(x) + sq(y) + sq(z)); }
	
	public static double sq(double f)
	{ return f * f; }
	
	public static double sq(double f, int i)
	{ if(i == 2) return sq(f); double f1 = 1F; for(int j = 0; j < i; j++)
	f1 *= f; return f1; }
	
	public static int power(int f, int n)
	{ int j = 1; for(int i = 0; i < n; i++) j *= f; return j; }

	public static double distSq(double x1, double y1, double z1, double x2, double y2, double z2)
	{ return (sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1)); }
	
	public static double dist(double x1, double y1, double z1, double x2, double y2, double z2)
	{ return sqrt(distSq(x1, y1, z1, x2, y2, z2)); }
	
	public static double distSq(double x1, double y1, double x2, double y2)
	{ return sq(x2 - x1) + sq(y2 - y1); }
	
	public static double dist(double x1, double y1, double x2, double y2)
	{ return sqrt(distSq(x1, y1, x2, y2)); }
	
	public static double distSq(Vertex v1, Vertex v2)
	{ return distSq(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z); }
	
	public static double dist(Vertex v1, Vertex v2)
	{ return sqrt(dist(v1, v2)); }
	
	public static Vertex getLook(Vertex v, double yaw, double pitch, double dist)
	{
		if(v == null) v = new Vertex();
		double f = cos(pitch * RAD);
		double x1 = cos(-yaw * RAD + HALF_PI);
        double z1 = sin(-yaw * RAD + HALF_PI);
        double y1 = sin(pitch * RAD);
        v.set(x1 * f * dist, y1 * dist, z1 * f * dist);
        return v;
	}
	
	public static int floor(double d)
	{ return (int) Math.floor(d); }
	
	public static int ceil(double d)
	{ return (int) Math.ceil(d); }
	
	public static int randomInt(Random r, int min, int max)
	{ return min + r.nextInt(max - min + 1); }
	
	public static double randomDouble(Random r, double min, double max)
	{ return min + r.nextDouble() * (max - min); }

	public static boolean isRound(double d)
	{ return Math.round(d) == d; }
	
	public static void fill(double[][] af, int size1, int size2, double f)
	{ for(int i = 0; i < size1; i++) for(int j = 0; j < size2; j++) af[i][j] = f; }
	
	public static void fill(int[][] ai, int size1, int size2, int i)
	{ for(int k = 0; k < size1; k++) for(int j = 0; j < size2; j++) ai[k][j] = i; }
	
	public static int lerpInt(int i1, int i2, double f)
	{ return i1 + (int)((i2 - i1) * f); }
	
	public static double lerp(double f1, double f2, double f)
	{ return f1 + (f2 - f1) * f; }
	
	public static double limit(double f, double min, double max)
	{ if(f == -0F) f = 0F; if(f < min) f = min; if(f > max) f = max; return f; }
	
	public static int limitInt(int i, int min, int max)
	{ if(i == -0) i = 0; if(i < min) i = min; if(i > max) i = max; return i; }
	
	public static double[] limit(double[] f, double min, double max)
	{ for(int i = 0; i < f.length; i++) f[i] = limit(f[i], min, max); return f; }
	
	public static int[] limitInt(int[] i, int min, int max)
	{ for(int j = 0; j < i.length; j++) i[j] = limitInt(i[j], min, max); return i; }
	
	public static int toIntDecoded(String s)
	{ return Integer.decode(s); }
	
	public static int toInt(String s)
	{ return Integer.parseInt(s.trim()); }
	
	public static double toDouble(String s)
	{ return Double.parseDouble(s.trim()); }
	
	public static int toInt(String s, int def)
	{ try { return toInt(s); } catch(Exception e) { return def; } }
	
	public static double toDouble(String s, double def)
	{ try { return toDouble(s); } catch(Exception e) { return def; } }
	
	public static double toSmallDouble(double f)
	{ long i = (long)(f * 100D); return i / 100D; }
	
	public static boolean canParseInt(String s)
	{
		try { Integer.parseInt(s); return true; }
		catch(Exception e) { } return false;
	}
	
	public static double map(double val, double min1, double max1, double min2, double max2)
	{ return min2 + (max2 - min2) * ((val - min1) / (max1 - min1)); }
	
	public static int mapInt(int val, int min1, int max1, int min2, int max2)
	{ return min2 + (max2 - min2) * ((val - min1) / (max1 - min1)); }
	
	public static double sinFromDeg(double f)
	{ return sin(f * RAD); }
	
	public static double cosFromDeg(double f)
	{ return cos(f * RAD); }
	
	public static double tanFromDeg(double f)
	{ return tan(f * RAD); }
	
	public static final Vertex getMidPoint(double x1, double y1, double z1, double x2, double y2, double z2, double p)
	{ double x = x2 - x1; double y = y2 - y1; double z = z2 - z1; double d = Math.sqrt(x * x + y * y + z * z);
	return new Vertex(x1 + (x / d) * (d * p), y1 + (y / d) * (d * p), z1 + (z / d) * (d * p)); }
	
	public static Vertex getMidPoint(Vertex v1, Vertex v2, double p)
	{ return getMidPoint(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, p); }
	
	public static boolean isPlural(int i) { String s = "" + i;
	return !(s.endsWith("1") && !s.endsWith("11")); }
}