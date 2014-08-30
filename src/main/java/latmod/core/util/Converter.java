package latmod.core.util;

public class Converter
{
	public static int[] toInts(byte[] b)
	{
		if(b == null) return null;
		int ai[] = new int[b.length];
		for(int i = 0; i < ai.length; i++)
		ai[i] = b[i] & 0xFF; return ai;
	}
	
	public static byte[] toBytes(int[] b)
	{
		if(b == null) return null;
		byte ai[] = new byte[b.length];
		for(int i = 0; i < ai.length; i++)
		ai[i] = (byte)b[i]; return ai;
	}
	
	public static Integer[] fromInts(int[] i)
	{
		if(i == null) return null;
		Integer ai[] = new Integer[i.length];
		for(int j = 0; j < ai.length; j++)
		ai[j] = i[j]; return ai;
	}
	
	public static int[] toInts(Integer[] i)
	{
		if(i == null) return null;
		int ai[] = new int[i.length];
		for(int j = 0; j < ai.length; j++)
		ai[j] = i[j]; return ai;
	}
	
	public static Double[] fromDoubles(double[] i)
	{
		if(i == null) return null;
		Double ai[] = new Double[i.length];
		for(int j = 0; j < ai.length; j++)
		ai[j] = i[j]; return ai;
	}
	
	public static double[] toDoubles(Double[] i)
	{
		if(i == null) return null;
		double ai[] = new double[i.length];
		for(int j = 0; j < ai.length; j++)
		ai[j] = i[j]; return ai;
	}
	
	public static Byte[] fromBytes(byte[] i)
	{
		if(i == null) return null;
		Byte ai[] = new Byte[i.length];
		for(int j = 0; j < ai.length; j++)
		ai[j] = i[j]; return ai;
	}
	
	public static byte[] toBytes(Byte[] i)
	{
		if(i == null) return null;
		byte ai[] = new byte[i.length];
		for(int j = 0; j < ai.length; j++)
		ai[j] = i[j]; return ai;
	}
	
	public static double[] toDoubles(float... f)
	{
		if(f == null) return null;
		double d[] = new double[f.length];
		for(int i = 0; i < f.length; i++)
		d[i] = f[i]; return d;
	}
	
	public static double[][] to2DArray(double[][][] f, int index, int lvl1size, int lvl2size)
	{
		double[][] a = new double[lvl1size][lvl2size];
		for(int j = 0; j < lvl2size; j++)
		for(int i = 0; i < lvl1size; i++)
		a[i][j] = f[index][i][j];
		return a;
	}
	
	public static double[] to1DArray(double[][] f, int index, int size)
	{
		double[] a = new double[size];
		for(int i = 0; i < size; i++)
		a[i] = f[index][i];
		return a;
	}
	
	public static int toInt(boolean[] b)
	{ int d = 0; for(int i = 0; i < b.length; i++)
	d |= (b[i] ? 1 : 0) << i; return d; }
	
	public static boolean[] toBool(int d)
	{ boolean b[] = new boolean[8]; for(int j = 0; j < 8; j++)
	b[j] = ((d >> j) & 1) == 1; return b; }
	
	public static int getBit(int bits, int i)
	{ return bits >> i; }
	
	public static boolean isBit(int bits, int i)
	{ return getBit(bits, i) == 1; }
	
	public static int toBit(boolean b, int i)
	{ return (b ? 1 : 0) << i; }
	
	public static boolean[] getBooleans(int bits, int q)
	{ boolean b[] = new boolean[q];
	for(int i = 0; i < q; i++)
	b[i] = isBit(bits, i); return b; }
	
	public static int toBits(boolean... b)
	{ int i = 0; for(int j = 0; j < b.length; j++)
	i |= toBit(b[j], j); return i; }
	
	public static Integer decode(String s)
	{
		try { Integer i = Integer.decode(s); return i; }
		catch(Exception e)
		{ } return null;
	}
}