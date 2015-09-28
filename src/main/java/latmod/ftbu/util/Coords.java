package latmod.ftbu.util;

import latmod.core.util.LMUtils;

public abstract class Coords
{
	public abstract Number getX();
	public abstract Number getY();
	
	public int hashCode()
	{ return LMUtils.hashCode(getX(), getY()); }
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		else if(o == this) return true;
		else if(o instanceof Coords)
		{
			Coords c = (Coords)o;
			return c.getX().equals(getX()) && c.getY().equals(getY());
		}
		
		return false;
	}
	
	public String toString()
	{ return getX() + ":" + getY(); }
	
	public static class I2 extends Coords
	{
		public final int x, y;
		
		public I2(int px, int py)
		{ x = px; y = py; }
		
		public Number getX()
		{ return Integer.valueOf(x); }
		
		public Number getY()
		{ return Integer.valueOf(y); }
	}
	
	public static class F2 extends Coords
	{
		public final float x, y;
		
		public F2(float px, float py)
		{ x = px; y = py; }
		
		public Number getX()
		{ return Float.valueOf(x); }
		
		public Number getY()
		{ return Float.valueOf(y); }
	}
	
	public static class D2 extends Coords
	{
		public final double x, y;
		
		public D2(double px, double py)
		{ x = px; y = py; }
		
		public Number getX()
		{ return Double.valueOf(x); }
		
		public Number getY()
		{ return Double.valueOf(y); }
	}
}