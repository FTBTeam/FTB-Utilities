package latmod.ftbu.core.util;

import java.util.Arrays;

/** Made by LatvianModder */
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
	
	public static Boolean[] fromBooleans(boolean[] i)
	{
		if(i == null) return null;
		Boolean ai[] = new Boolean[i.length];
		for(int j = 0; j < ai.length; j++)
		ai[j] = i[j]; return ai;
	}
	
	public static boolean[] toInts(Boolean[] i)
	{
		if(i == null) return null;
		boolean ai[] = new boolean[i.length];
		for(int j = 0; j < ai.length; j++)
		ai[j] = i[j]; return ai;
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
	
	public static void toBools(boolean[] bools, int[] idx, boolean isTrue)
	{
		Arrays.fill(bools, !isTrue);
		for(int i = 0; i < idx.length; i++)
			bools[idx[i]] = isTrue;
	}
	
	public static int[] fromBools(boolean[] bools, boolean isTrue)
	{
		IntList il = new IntList();
		for(int i = 0; i < bools.length; i++)
			if(bools[i] == isTrue) il.add(i);
		return il.toArray();
	}

	public static Integer toInt(String text)
	{
		try { Integer i = Integer.parseInt(text); return i; }
		catch(Exception e) { } return null;
	}
	
	public static Float toFloat(String text)
	{
		try { Float f = Float.parseFloat(text); return f; }
		catch(Exception e) { } return null;
	}
}