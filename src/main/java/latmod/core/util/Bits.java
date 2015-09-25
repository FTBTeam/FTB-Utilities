package latmod.core.util;

/** Made by LatvianModder */
public class Bits
{
	private static final int FF = 0xFF;
	private static final int FFFF = 0xFFFF;
	private static final long FFFFFFFF = 0xFFFFFFFFL;
	
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
	{ int ai = FF; for(int j = 0; j < i.length; j++)
	ai &= i[j]; return ai; }
	
	public static int or(int... i)
	{ int ai = 0; for(int j = 0; j < i.length; j++)
	ai |= i[j]; return ai; }
	
	//
	
	//Int
	public static long intsToLong(int a, int b)
	{ return (((long)a) << 32) | (b & FFFFFFFF); }
	
	public static int intFromLongA(long l)
	{ return (int)(l >> 32); }
	
	public static int intFromLongB(long l)
	{ return (int)l; }
	
	//Short
	public static int shortsToInt(int a, int b)
	{ return ((short)a << 16) | ((short)b & FFFF); }
	
	public static short shortFromIntA(int i)
	{ return (short)(i >> 16); }
	
	public static short shortFromIntB(int i)
	{ return (short)(i & FFFF); }
	
	//Byte
	public static short bytesToShort(int a, int b)
	{ return (short)(((a & FF) << 8) | (b & FF)); }
	
	public static byte byteFromShortA(short s)
	{ return (byte)((s >> 8) & FF); }
	
	public static byte byteFromShortB(short s)
	{ return (byte)(s & FF); }
	
	// - //
	
	public static int toUShort(byte[] b)
	{
		int ch1 = b[0] & FF;
		int ch2 = b[1] & FF;
		return (ch1 << 8) + (ch2 << 0);
	}
	
	public static int toInt(byte[] b)
	{
		int ch1 = b[0] & FF;
		int ch2 = b[1] & FF;
		int ch3 = b[2] & FF;
		int ch4 = b[3] & FF;
		return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
	}
	
	public static long toLong(byte[] b)
	{
		return (((long)b[0] << 56)
		+ ((long)(b[1] & FF) << 48)
		+ ((long)(b[2] & FF) << 40)
		+ ((long)(b[3] & FF) << 32)
		+ ((long)(b[4] & FF) << 24)
		+ ((b[5] & FF) << 16)
		+ ((b[6] & FF) << 8)
		+ ((b[7] & FF) << 0));
	}
	
	// - //
	
	public static void fromUShort(byte[] b, int v)
	{
		b[0] = (byte)(v >>> 8);
		b[1] = (byte)(v >>> 0);
	}
	
	public static void fromInt(byte[] b, int v)
	{
		b[0] = (byte)(v >>> 24);
		b[1] = (byte)(v >>> 16);
		b[2] = (byte)(v >>> 8);
		b[3] = (byte)(v >>> 0);
	}
	
	public static void fromLong(byte[] b, long v)
	{
		b[0] = (byte)(v >>> 56);
		b[1] = (byte)(v >>> 48);
		b[2] = (byte)(v >>> 40);
		b[3] = (byte)(v >>> 32);
		b[4] = (byte)(v >>> 24);
		b[5] = (byte)(v >>> 16);
		b[6] = (byte)(v >>> 8);
		b[7] = (byte)(v >>> 0);
	}
}