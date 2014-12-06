package latmod.core.util;
import java.util.Random;

import net.minecraft.util.AxisAlignedBB;

/** Made by LatvianModder */
public class MathHelper // Converter
{
	public static final Random rand = new Random();
	public static final double RAD = Math.PI / 180D;
	public static final double DEG = 180D / Math.PI;
	public static final double TWO_PI = Math.PI * 2D;
	public static final double HALF_PI = Math.PI / 2D;
	
	public static double sin(double d)
	{ return net.minecraft.util.MathHelper.sin((float)d); }
	
	public static double cos(double d)
	{ return net.minecraft.util.MathHelper.cos((float)d); }
	
	public static double tan(double d)
	{ return sin(d) / cos(d); }
	
	public static double sinFromDeg(double f)
	{ return sin(f * RAD); }
	
	public static double cosFromDeg(double f)
	{ return cos(f * RAD); }
	
	public static double tanFromDeg(double f)
	{ return tan(f * RAD); }
	
	/** atan2 using Vertex */
	public static double atan2(Vertex pos, Vertex tar)
	{ if(pos == null || tar == null) return -1F;
	return Math.atan2(pos.x - tar.x, pos.z - tar.z); }
	
	/** atan using Vertex */
	public static double atan(Vertex pos, Vertex tar)
	{ if(pos == null || tar == null) return -1F;
	return Math.atan(pos.y - tar.y); }
	
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
	
	public static double sq(double f, int i)
	{ if(i == 2) return sq(f); double f1 = 1F; for(int j = 0; j < i; j++)
	f1 *= f; return f1; }
	
	public static int power(int f, int n)
	{ int j = 1; for(int i = 0; i < n; i++) j *= f; return j; }

	public static double distSq(double x1, double y1, double z1, double x2, double y2, double z2)
	{ return (sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1)); }
	
	public static double dist(double x1, double y1, double z1, double x2, double y2, double z2)
	{ return sqrt(distSq(x1, y1, z1, x2, y2, z2)); }
	
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
	{ return net.minecraft.util.MathHelper.floor_double(d); }
	
	public static int chunk(double d)
	{ return floor(d) >> 4; }
	
	public static int ceil(double d)
	{ return net.minecraft.util.MathHelper.ceiling_double_int(d); }
	
	public static int randomInt(Random r, int min, int max)
	{ return min + r.nextInt(max - min + 1); }
	
	public static double randomDouble(Random r, double min, double max)
	{ return min + r.nextDouble() * (max - min); }

	public static boolean isRound(double d)
	{ return Math.round(d) == d; }
	
	public static int lerpInt(int i1, int i2, double f)
	{ return i1 + (int)((i2 - i1) * f); }
	
	public static double lerp(double f1, double f2, double f)
	{ return f1 + (f2 - f1) * f; }
	
	public static double clamp(double d, double min, double max)
	{ if(d < min) d = min; if(d > max) d = max; return d; }
	
	public static int clampInt(int i, int min, int max)
	{ if(i < min) i = min; if(i > max) i = max; return i; }
	
	public static double[] clamp(double[] d, double min, double max)
	{ for(int i = 0; i < d.length; i++) d[i] = clamp(d[i], min, max); return d; }
	
	public static int[] clampInt(int[] i, int min, int max)
	{ for(int j = 0; j < i.length; j++) i[j] = clampInt(i[j], min, max); return i; }
	
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
	
	public static final Vertex getMidPoint(double x1, double y1, double z1, double x2, double y2, double z2, double p)
	{ double x = x2 - x1; double y = y2 - y1; double z = z2 - z1; double d = Math.sqrt(x * x + y * y + z * z);
	return new Vertex(x1 + (x / d) * (d * p), y1 + (y / d) * (d * p), z1 + (z / d) * (d * p)); }
	
	public static Vertex getMidPoint(Vertex v1, Vertex v2, double p)
	{ return getMidPoint(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, p); }
	
	public static boolean isPlural(int i) { String s = "" + i;
	return !(s.endsWith("1") && !s.endsWith("11")); }
	
	public static String getPluralWord(int i, String a, String b)
	{ return isPlural(i) ? a : b; }
	
	public static String formatDouble(double d)
	{
		String s = "" + d;
		if(s.endsWith(".0"))
			s = s.substring(0, s.length() - 2);
		return s;
	}
	
	public static AxisAlignedBB getBox(double cx, double y0, double cz, double w, double y1, double d)
	{ return AxisAlignedBB.getBoundingBox(cx - w / 2D, y0, cz - d / 2D, cx + w / 2D, y1, cz + d / 2D); }
}