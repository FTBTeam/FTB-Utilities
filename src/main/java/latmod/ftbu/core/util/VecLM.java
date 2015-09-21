package latmod.ftbu.core.util;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;

/** Made by LatvianModder */
public final class VecLM implements Cloneable
{
	public double x;
	public double y;
	public double z;
	
	public VecLM() { }
	
	public VecLM(double nx, double ny, double nz)
	{ x = nx; y = ny; z = nz; }
	
	public VecLM(Entity e, boolean y)
	{ this(e.posX, y ? e.posY : 0D, e.posZ); }
	
	public VecLM(Entity e)
	{ this(e, true); }
	
	public VecLM(ChunkCoordinates c, boolean y)
	{ this(c.posX  + 0.5D, (y ? c.posY : 0D) + 0.5D, c.posZ + 0.5D); }
	
	public VecLM(ChunkCoordinates c)
	{ this(c, true); }
	
	public VecLM(Random r, boolean sin)
	{
		this(r.nextFloat(), r.nextFloat(), r.nextFloat());
		if(sin) { scale(2D); add(-1D, -1D, -1D); }
	}
	
	public void set(double nx, double ny, double nz)
	{ x = nx; y = ny; z = nz; }
	
	public void set(VecLM v)
	{ set(v.x, v.y, v.z); }
	
	public void add(double ax, double ay, double az)
	{ set(x + ax, y + ay, z + az); }
	
	public void add(VecLM v, double s)
	{ add(v.x * s, v.y * s, v.z * s); }
	
	public void scale(double sx, double sy, double sz)
	{ x *= sx; y *= sy; z *= sz; }
	
	public void scale(VecLM v, double s)
	{ scale(v.x * s, v.y * s, v.z * s); }
	
	public void scale(double s)
	{ scale(s, s, s); }
	
	public boolean isNull()
	{ return x == 0D && y == 0D && z == 0D; }
	
	public boolean containsNaN()
	{ return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(y); }
	
	public boolean equals(Object o)
	{ return (o instanceof VecLM && equalsPos((VecLM)o)); }
	
	public boolean equalsPos(VecLM v)
	{ return v.x == x && v.y == y && v.z == z; }
	
	public VecLM clone()
	{ return new VecLM(x, y, z); }
	
	public double distSq(double x1, double y1, double z1)
	{ return MathHelperLM.sq(x1 - x) + MathHelperLM.sq(y1 - y) + MathHelperLM.sq(z1 - z); }
	
	public double dist(double x1, double y1, double z1)
	{ return MathHelperLM.sqrt(distSq(x1, y1, z1)); }
	
	public double distSq(VecLM v)
	{ return distSq(v.x, v.y, v.z); }
	
	public double dist(VecLM v)
	{ return MathHelperLM.sqrt(distSq(v)); }
	
	public double atan2(VecLM v)
	{
		if(v == null) return -1D;
		return Math.atan2(x - v.x, z - v.z);
	}
	
	public double atan2Y(VecLM v)
	{
		if(v == null) return -1D;
		return Math.atan2(x - v.x, y - v.y);
	}
	
	public double atan(VecLM v)
	{
		if(v == null) return -1D;
		return Math.atan(y - v.y);
	}
	
	public int getX()
	{ return MathHelperLM.floor(x); }
	
	public int getY()
	{ return MathHelperLM.floor(y); }
	
	public int getZ()
	{ return MathHelperLM.floor(z); }
	
	public double length()
	{ return Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z))); }
	
	public VecLM normalize()
	{ double d = length(); return new VecLM(x / d, y / d, z / d); }
}