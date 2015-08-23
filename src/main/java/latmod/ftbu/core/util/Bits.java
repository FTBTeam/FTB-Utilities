package latmod.ftbu.core.util;

/** Made by LatvianModder */
public class Bits
{
	public static int toInt(boolean[] b)
	{ int d = 0; for(int i = 0; i < b.length; i++)
	d |= (b[i] ? 1 : 0) << i; return d; }
	
	public static boolean[] toBool(int d)
	{ boolean b[] = new boolean[8]; for(int j = 0; j < 8; j++)
	b[j] = ((d >> j) & 1) == 1; return b; }
	
	public static int getBit(int bits, int i)
	{ return (bits >> i) & 1; }
	
	public static boolean isBit(int bits, int i)
	{ return getBit(bits, i) == 1; }
	
	public static int toBit(boolean b, int i)
	{ return (b ? 1 : 0) << i; }
	
	public static boolean[] fromBits(int bits, int q)
	{ boolean b[] = new boolean[q];
	for(int i = 0; i < q; i++)
	b[i] = isBit(bits, i); return b; }
	
	public static int toBits(boolean... b)
	{ int i = 0; for(int j = 0; j < b.length; j++)
	i |= toBit(b[j], j); return i; }
	
	public static boolean[] not(boolean... b)
	{
		boolean[] ab = new boolean[b.length];
		for(int i = 0; i < ab.length; i++)
		ab[i] = !b[i]; return ab;
	}
	
	public static int not(int i)
	{ return toBits(not(fromBits(i, 8))); }
	
	public static int and(int... i)
	{ int ai = 255; for(int j = 0; j < i.length; j++)
	ai &= i[j]; return ai; }
	
	public static int or(int... i)
	{ int ai = 0; for(int j = 0; j < i.length; j++)
	ai |= i[j]; return ai; }
	
	//
	
	public static long intToLong(int a, int b)
	{ return (((long)a) << 32) | (b & 0xFFFFFFFFL); }
	
	public static int intFromLongA(long l)
	{ return (int)(l >> 32); }
	
	public static int intFromLongB(long l)
	{ return (int)l; }
	
	/*
	
	public static int shortToInt(int a, int b)
	{ return 0; }
	
	public static short shortFromInt(int l, boolean a)
	{ return (short)0; }
	
	*/
	
	public static short byteToShort(int a, int b)
	{ return (short)(((a & 0xFF) << 8) | (b & 0xFF)); }
	
	public static byte byteFromShortA(short s)
	{ return (byte)((s >> 8) & 0xFF); }
	
	public static byte byteFromShortB(short s)
	{ return (byte)(s & 0xFF); }
}