package latmod.core;

import latmod.core.util.*;

public class Version implements Comparable<Version>
{
	public final int major;
	public final int minor;
	public final int rev;
	private final int hashCode;
	private final String toString;
	
	public Version(int a, int i, int r)
	{
		major = a;
		minor = i;
		rev = r;
		
		hashCode = LatCore.hashCode(major, minor, rev);
		toString = "" + major + '.' + minor + '.' + rev;
	}
	
	public static final Version parseVersion(String s)
	{
		if(s == null || s.isEmpty()) return null;
		
		String[] s1 = LatCore.split(s, "\\.");
		
		if(s1 != null && s1.length > 0 && s1.length <= 4)
		{
			int a = MathHelperLM.toInt(s1[0], 1);
			int i = (s1.length >= 2) ? MathHelperLM.toInt(s1[1], 0) : 0;
			int r = (s1.length >= 3) ? MathHelperLM.toInt(s1[2], 0) : 0;
			return new Version(a, i, r);
		}
		
		return null;
	}
	
	public String toString()
	{ return toString; }
	
	public int hashCode()
	{ return hashCode; }
	
	public boolean equals(Object o)
	{ if(o == null) return false; if(o == this) return true;
	return o.getClass() == Version.class && equalsVersion((Version)o); }
	
	public boolean equalsVersion(Version v)
	{ return major == v.major && minor == v.minor && rev == v.rev; }
	
	public int compareTo(Version v)
	{
		if(equalsVersion(v)) return 0;
		
		int c = Integer.compare(major, v.major);
		
		if(c == 0)
		{
			c = Integer.compare(minor, v.minor);
			if(c == 0) return Integer.compare(rev, v.rev);
			return c;
		}
		return c;
	}
	
	public Version copy()
	{ return new Version(major, minor, rev); }
}