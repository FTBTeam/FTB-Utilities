package latmod.core;

public class Bits
{
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
}